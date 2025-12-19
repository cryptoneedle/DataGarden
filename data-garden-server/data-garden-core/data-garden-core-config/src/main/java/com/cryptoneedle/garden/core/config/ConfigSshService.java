package com.cryptoneedle.garden.core.config;

import cn.hutool.v7.extra.ssh.Connector;
import cn.hutool.v7.extra.ssh.engine.sshj.SshjSession;
import cn.hutool.v7.extra.ssh.engine.sshj.SshjUtil;
import com.cryptoneedle.garden.core.crud.config.*;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Parameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * <p>description: 配置-隧道配置-服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-08
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class ConfigSshService {
    
    public final AddConfigService add;
    public final SelectConfigService select;
    public final SaveConfigService save;
    public final DeleteConfigService delete;
    public final PatchConfigService patch;
    
    public ConfigSshService(AddConfigService addConfigService,
                            SelectConfigService selectConfigService,
                            SaveConfigService saveConfigService,
                            DeleteConfigService deleteConfigService,
                            PatchConfigService patchConfigService) {
        this.add = addConfigService;
        this.select = selectConfigService;
        this.save = saveConfigService;
        this.delete = deleteConfigService;
        this.patch = patchConfigService;
    }
    
    public void test(ConfigSsh configSsh) {
        String localHost = "127.0.0.1";
        int localPort = 35000;
        String targetHost = "10.36.10.198";
        int targetPort = 1521;
        
        Connector connector = Connector.of(configSsh.getId().getHost(), configSsh.getPort(), configSsh.getUsername(), configSsh.getPassword());
        
        try (SSHClient sshClient = SshjUtil.openClient(connector)) {
            sshClient.getConnection().getKeepAlive().setKeepAliveInterval(5);
            
            Parameters parameters = new Parameters(localHost, localPort, targetHost, targetPort);
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(localHost, localPort));
            
            sshClient.newLocalPortForwarder(parameters, serverSocket);
        } catch (Exception e) {
        
        }
        
        try (SshjSession sshSession = new SshjSession(connector)) {
            sshSession.bindLocalPort(localPort, InetSocketAddress.createUnresolved(targetHost, targetPort));
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static void main() {
        String sshHost = "10.36.10.92";
        int sshPort = 4500;
        String sshUser = "ythyysjcj";
        String sshPassword = "\\xoff}zg{[44[6zdgc";
        
        int localPort = 35000;
        String targetHost = "10.36.10.198";
        int targetPort = 1521;
        
        Connector connector = Connector.of(sshHost, sshPort, sshUser, sshPassword);
        try (SshjSession sshSession = new SshjSession(connector)) {
            sshSession.bindLocalPort(localPort, InetSocketAddress.createUnresolved(targetHost, targetPort));
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}