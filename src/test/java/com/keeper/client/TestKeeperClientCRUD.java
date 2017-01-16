package com.keeper.client;



import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.keeper.client.KeeperClient;
import com.keeper.client.exception.KeeperException;
import com.keeper.server.KeeperSimpleServer;

/**
 *@author huangdou
 *@at 2016年12月2日上午10:43:36
 *@version 0.0.1
 */
public class TestKeeperClientCRUD {
	
	KeeperClient client ;
	
	KeeperSimpleServer server ;
	@Before
	public void beforeTest(){
		server = new KeeperSimpleServer("d:\\zktmp\\snap", "d:\\zktmp\\datalog");
		server.startZkServer();
		client = new KeeperClient("127.0.0.1:2181");
	}
	@After
	public void afterTest(){
		if (client != null){
			client.closeClient();
			client = null;
		}
		if (server!=null){
			server.shutdown();
		}
	}

	@Test
	public void testExist() {
		String path = "/test1";
		if (client.exist(path)){
			client.delete(path);
		}
		client.create(path, "".getBytes());
		Assert.assertTrue(client.exist(path));
		client.delete(path);
	}
	
	@Test
	public void testCreate() {
		String path1 = "/test1";
		String path2 = "/test2";
		String path3 = "/test3";
		client.create(path1, "".getBytes());
		client.create(path2, "".getBytes(), CreateMode.PERSISTENT);
		client.create(path3, "".getBytes(), CreateMode.EPHEMERAL);
		
		try{
			client.create(path1, "".getBytes());
		}catch(KeeperException e){
			Assert.assertTrue(e.getProto() instanceof NodeExistsException);
		}
		
		
		Assert.assertTrue(client.exist(path1));
		Assert.assertTrue(client.exist(path2));
		Assert.assertTrue(client.exist(path3));
		
		client.delete(path1);
		client.delete(path2);
		client.delete(path3);
	}
	
	@Test
	public void testRead() {
		
		String path1 = "/test1";
		String data = "hello world";
		if (client.exist(path1)){
			client.delete(path1);
		}
		client.create(path1, data.getBytes());
		byte[] bytes = client.read(path1);
		Assert.assertEquals(new String(bytes), data);
		
		client.delete(path1);
	}
	
	@Test
	public void testUpdate() {
		String path1 = "/test1";
		String data = "hello world";
		String updates = "updatedData";
		
		if (client.exist(path1)){
			client.delete(path1);
		}
		client.create(path1, data.getBytes());
		client.update(path1, updates.getBytes());
		
		byte[] bytes = client.read(path1);
		Assert.assertEquals(new String(bytes), updates);
	}
	

}
