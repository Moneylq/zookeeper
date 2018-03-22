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

import lombok.Getter;
import lombok.Setter;

public class WatchMore extends BaseConfig{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WatchMore.class);

	private @Setter@Getter String oldValue = "";
	private @Setter@Getter String newValue = "";
	
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
		oldValue = result;
		return result;
	}
	
	public boolean triggerValue(String path) throws Exception {
		
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
		newValue = result;
		
		if(newValue.equals(oldValue)) {
			logger.info("**********The Value no changes********");
			return false;
		} else {
			logger.info("***********The oldValue : " + oldValue + "\t newValue : " + newValue);
			oldValue = newValue;
			return true;
		}
	}
	
	public static void main(String[] args) throws Exception {

		WatchMore watchMore = new WatchMore();
		watchMore.setZk(watchMore.startZK());
		
		if(watchMore.getZk().exists(PATH, false) == null) {
			watchMore.createZnode(PATH, "AAA");
			String result = watchMore.getZnode(PATH);
			
			if(logger.isInfoEnabled()) {
				logger.info("main(String[])============init String result = " + result);
			}
		} else {
			logger.info("This node has already exists....");
		}
		
		Thread.sleep(Long.MAX_VALUE);
		
	}

}
