package com.briansjavablog.locking.explicit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ReadWriteReentrantLockTransactionLog {
	
	private Map<String, String> transactionHistory = new HashMap<>();
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	
	
	public void saveTransaction(String transaction) throws InterruptedException{
		try{
			readWriteLock.writeLock().lock();
			transactionHistory.put(UUID.randomUUID().toString(), transaction);	
			System.out.println("Saved Transaction: " + transaction);
		}
		finally{
			readWriteLock.writeLock().unlock();	
		}		
	}
	
	public List<String> getTransactions(){		
		
		List<String> transactions = null;
				
		try{
			readWriteLock.readLock().lock();
			transactions =  transactionHistory.values().stream().collect(Collectors.toList());			
		}
		finally{
			readWriteLock.readLock().unlock();	
		}	
		
		return transactions;
	}	
}