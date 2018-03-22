package top.idalin;

import org.apache.zookeeper.ZooKeeper;

import lombok.Getter;
import lombok.Setter;

public class BaseConfig {

	// 实例常量
	public static final String CONNECTIONSTRING = "192.168.23.122:2181";
	public static final String PATH = "/zDalin";
	public static final int SESSION_TIMEOUT = 20 * 1000;
	public @Setter@Getter ZooKeeper zk;
	
	/**
	 * 关闭连接
	 * @param zk
	 * @throws InterruptedException
	 */
	public void stopZK(ZooKeeper zk) throws InterruptedException {
		if(null != zk) zk.close();
	}

}
