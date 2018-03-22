package top.idalin;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/***
 * 只监控一次
 * 牵一发而动全身....
 * @author 123
 *
 */
public class WatchOne extends BaseConfig {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WatchOne.class);

	/**
	 * Logger for this class
	 */
	// private static final Logger logger = Logger.getLogger(HelloZK.class);
	
	
	/**
	 * 获得ZooKeeper的连接实例
	 * @return
	 * @throws IOException 
	 */
	public ZooKeeper startZK() throws IOException {
		
		return new ZooKeeper(CONNECTIONSTRING, SESSION_TIMEOUT, new Watcher() {
			
			@Override
			public void process(WatchedEvent arg0) {
			}
		});
		
	}
	/**
	 * 创建一个节点，并赋值
	 * @param zk
	 * @param path
	 * @param data
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void createZnode(String path,String data) throws KeeperException, InterruptedException {
		zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	/**
	 * 获得节点的值
	 * @param zk
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public String getZnode(String path) throws KeeperException, InterruptedException {
		String result = "";
		byte[] byteArray = zk.getData(path, new Watcher() {
			
			@Override
			public void process(WatchedEvent arg0) {
				
				// 业务逻辑，也即触发了/zDalin节点后的变更后，我需要立刻获得最新值
				// 将本部分的业务逻辑提取出来，新变成一个方法
				try {
					triggerValue(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new Stat());
		result = new String(byteArray);
		return result;
	}
	
	public void triggerValue(String path) throws Exception {
		
		String result = "";
		byte[] byteArray = zk.getData(path, null, new Stat());
		result = new String(byteArray);

		logger.info("************watcher after triggerValue result : " + result);
	}
	
	public static void main(String[] args) throws Exception {

		WatchOne one = new WatchOne();
		one.setZk(one.startZK());
		
		if(one.getZk().exists(PATH, false) == null) {
			one.createZnode(PATH, "AAA");
			
			String result = one.getZnode(PATH);
			
			logger.info("************main init result : " + result);
		} else {
			logger.info("This node is exists...");
		}
		Thread.sleep(Long.MAX_VALUE);
	}

}
