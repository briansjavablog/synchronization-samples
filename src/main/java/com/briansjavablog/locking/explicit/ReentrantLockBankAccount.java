package com.briansjavablog.locking.explicit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockBankAccount {
	
	private double balance;
	private Lock lock = new ReentrantLock(true);
	
	
	public void credit(double amount){
		try{			
			lock.lock();
			balance = balance + amount;				
		}
		finally{
			lock.unlock();	
		}		
	}
	
	public void debit(double amount) throws InterruptedException{		
		
		if(lock.tryLock(2000, TimeUnit.MILLISECONDS)){
			try{			
				balance = balance - amount;			
			}
			finally{
			   lock.unlock();	
			}
		}
		/* lock isn't available right now so do something else */
		else{
			/* add debit amount to queue for processing later */
		}				
	}
		
	public double getBalance(){
		return balance;
	}
	
}