package com.cryptoneedle.garden.spi;

import cn.hutool.v7.core.io.IoUtil;
import cn.hutool.v7.core.net.NetUtil;
import cn.hutool.v7.extra.ssh.Connector;
import cn.hutool.v7.extra.ssh.engine.sshj.SshjUtil;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.direct.Parameters;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>description: 隧道工具 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-18
 */
@Slf4j
public class SshUtil {
    
    // key: sshHost value:SSHClient
    private static final Map<String, SSHClient> SSH_CLIENT_MAP = new ConcurrentHashMap<>();
    // key: remoteHost value:remotePort localPort
    private static final Map<String, Map<Integer, Integer>> FORWARD_MAP = new ConcurrentHashMap<>();
    
    public synchronized static Integer getForwardPort(SourceCatalog sourceCatalog) {
        ConfigSsh configSsh = sourceCatalog.getConfigSsh();
        if (configSsh == null) {
            throw new RuntimeException("configSsh is null");
        }
        String remoteHost = sourceCatalog.getHost();
        Integer remotePort = sourceCatalog.getPort();
        
        // 检查是否已经建立了端口转发
        Map<Integer, Integer> forwardPortMap = FORWARD_MAP.get(remoteHost);
        if (forwardPortMap != null) {
            Integer localPort = forwardPortMap.get(remotePort);
            if (localPort != null) {
                log.debug("[SSH] 使用已存在的端口转发: {}:{} -> localhost:{}", remoteHost, remotePort, localPort);
                return localPort;
            }
        }
        
        // 创建SSH连接
        String sshHost = configSsh.getId().getHost();
        SSHClient sshClient = SSH_CLIENT_MAP.computeIfAbsent(sshHost, v -> createSshClient(configSsh));
        if (!sshClient.isConnected()) {
            try {
                log.warn("[SSH] SSH连接已断开，正在重新连接: {}", sshHost);
                sshClient.disconnect();
                IoUtil.closeQuietly(sshClient);
                SSH_CLIENT_MAP.remove(sshHost);
                sshClient = createSshClient(configSsh);
                SSH_CLIENT_MAP.put(sshHost, sshClient);
            } catch (Exception e) {
                log.error("[SSH] 重新建立SSH连接失败: {}", sshHost, e);
                throw new RuntimeException("重新建立SSH连接失败: " + sshHost, e);
            }
        }
        
        // 获取一个可用的本地端口
        int localPort = NetUtil.getUsableLocalPort(35000);
        log.info("[SSH] 分配本地端口: {}", localPort);
        
        // 建立端口转发
        try {
            Parameters params = new Parameters(
                "127.0.0.1",  // 本地监听地址
                localPort,     // 本地监听端口
                remoteHost,    // 远程主机地址
                remotePort     // 远程主机端口
            );
            
            log.info("[SSH] 建立端口转发: localhost:{} -> {}:{} (via {})", 
                localPort, remoteHost, remotePort, sshHost);
            
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress("127.0.0.1", localPort));
            
            // 在后台线程中启动端口转发
            final SSHClient finalSshClient = sshClient;
            Thread forwarderThread = new Thread(() -> {
                try {
                    LocalPortForwarder forwarder = finalSshClient.newLocalPortForwarder(params, serverSocket);
                    forwarder.listen();
                } catch (IOException e) {
                    log.error("[SSH] 端口转发异常: localhost:{} -> {}:{}", localPort, remoteHost, remotePort, e);
                }
            });
            forwarderThread.setDaemon(true);
            forwarderThread.setName("SSH-PortForward-" + remoteHost + ":" + remotePort);
            forwarderThread.start();
            
            // 等待一小段时间确保端口转发已经启动
            Thread.sleep(500);
            
            // 缓存端口映射
            FORWARD_MAP.computeIfAbsent(remoteHost, v -> new ConcurrentHashMap<>(1))
                      .put(remotePort, localPort);
            
            log.info("[SSH] 端口转发建立成功: localhost:{} -> {}:{}", localPort, remoteHost, remotePort);
            return localPort;
        } catch (Exception e) {
            log.error("[SSH] 建立端口转发失败: localhost:{} -> {}:{}", localPort, remoteHost, remotePort, e);
            throw new RuntimeException("建立SSH端口转发失败: " + remoteHost + ":" + remotePort, e);
        }
    }
    
    private synchronized static SSHClient createSshClient(ConfigSsh configSsh) {
        String sshHost = configSsh.getId().getHost();
        int sshPort = configSsh.getPort();
        String username = configSsh.getUsername();
        
        log.info("[SSH] 建立SSH连接: {}@{}:{}", username, sshHost, sshPort);
        
        Connector connector = Connector.of(
                sshHost,
                sshPort,
                username,
                configSsh.getPassword()
        );
        
        try {
            SSHClient sshClient = SshjUtil.openClient(connector);
            sshClient.getConnection().getKeepAlive().setKeepAliveInterval(15); // 心跳 15s
            log.info("[SSH] SSH连接建立成功: {}@{}:{}", username, sshHost, sshPort);
            return sshClient;
        } catch (Exception e) {
            log.error("[SSH] SSH连接建立失败: {}@{}:{}", username, sshHost, sshPort, e);
            throw new RuntimeException("SSH连接建立失败: " + sshHost + ":" + sshPort, e);
        }
    }
}