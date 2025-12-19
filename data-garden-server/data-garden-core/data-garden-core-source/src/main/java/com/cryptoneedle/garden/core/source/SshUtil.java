package com.cryptoneedle.garden.core.source;

import cn.hutool.v7.core.io.IoUtil;
import cn.hutool.v7.core.net.NetUtil;
import cn.hutool.v7.extra.ssh.Connector;
import cn.hutool.v7.extra.ssh.engine.sshj.SshjUtil;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import net.schmizz.sshj.SSHClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>description: 隧道工具 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-18
 */
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
        String host = sourceCatalog.getHost();
        Map<Integer, Integer> forwardPortMap = FORWARD_MAP.get(host);
        if (forwardPortMap != null) {
            Integer localPort = forwardPortMap.get(sourceCatalog.getPort());
            if (localPort != null) {
                return localPort;
            }
        }
        
        int localPort = NetUtil.getUsableLocalPort(35000, 40000);
        
        // 创建连接
        String sshHost = configSsh.getId().getHost();
        SSHClient sshClient = SSH_CLIENT_MAP.computeIfAbsent(sshHost, v -> createSshClient(configSsh));
        if (!sshClient.isConnected()) {
            try {
                sshClient.disconnect();
                // 重新创建连接
                IoUtil.closeQuietly(sshClient);
                SSH_CLIENT_MAP.remove(sshHost);
                createSshClient(configSsh);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return FORWARD_MAP.computeIfAbsent(host, v -> new ConcurrentHashMap<>(1))
                         .computeIfAbsent(sourceCatalog.getPort(), v -> localPort);
    }
    
    private synchronized static SSHClient createSshClient(ConfigSsh configSsh) {
        Connector connector = Connector.of(configSsh.getId()
                                                    .getHost(), configSsh.getPort(), configSsh.getUsername(), configSsh.getPassword());
        SSHClient sshClient = SshjUtil.openClient(connector);
        sshClient.getConnection().getKeepAlive().setKeepAliveInterval(15); // 心跳 15s
        SSH_CLIENT_MAP.putIfAbsent(configSsh.getId().getHost(), sshClient);
        return sshClient;
    }
}