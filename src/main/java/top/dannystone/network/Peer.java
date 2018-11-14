package top.dannystone.network;

import bizsocket.tcp.Packet;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-14
 * Time: 下午11:38
 */
public interface Peer {
    void send();

    /**
     * //todo 暂时使用bizsocket 的packet，不利于后续解耦
     * @return
     */
    Packet receive();
}
