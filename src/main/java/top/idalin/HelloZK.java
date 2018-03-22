package top.idalin;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 1. 通过java程序，新建链接zk,类似jdbc中的Connection，open,session
 * 2. 新建一个znode节点，/zDalin并赋值为2018，等同于 create /zDalin 2018
 * 3. 获取当前节点/zDalin的最新值，   get /zDalin
 * 4. 关闭连接
 *
 */
public class HelloZK extends BaseConfig
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(HelloZK.class);
	
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
		byte[] byteArray = zk.getData(path, false, new Stat());
		result = new String(byteArray);
		return result;
	}
	
    public static void main(String[] args ) throws Exception
    {

    	HelloZK hello = new HelloZK();
    	ZooKeeper zk = hello.startZK();
    	
    	if(zk.exists(PATH, false) == null) {
    		hello.createZnode(PATH, "hello zookeeper");
    		String result = hello.getZnode(PATH);
    		
			if (logger.isInfoEnabled()) {
				logger.info("main(String[]) -------------- String result=" + result);
			}
    	} else {
    		logger.info("this node is already exists");
    	}
    	
    	hello.stopZK(zk);
    }
}
