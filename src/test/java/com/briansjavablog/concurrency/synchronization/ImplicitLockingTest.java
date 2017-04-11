package com.briansjavablog.concurrency.synchronization;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;

import com.briansjavablog.locking.implict.BankAccount;
import com.briansjavablog.locking.implict.SynchronizedBankAccount;

public class ImplicitLockingTest {
	
	
	@Test
	public void testBankAccountWithoutSynchronization(){
		
		BankAccount bankAccount = new BankAccount();
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		IntStream.rangeClosed(1, 100).forEach(i->{			
			executorService.submit(()-> {			
				bankAccount.debit(100);				
			});
			
			executorService.submit(()-> {				
				bankAccount.credit(100);				
			});
		});
		
		executorService.shutdown();				
		System.out.println("Final Balance: " + bankAccount.getBalance());		
	}
	
	
	@Test
	public void testSynchronizedBankAccount() throws InterruptedException{
		
		SynchronizedBankAccount bankAccount = new SynchronizedBankAccount();
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		IntStream.rangeClosed(1, 1000).forEach(i->{			
			executorService.submit(()-> {			
				bankAccount.debit(100);			
			});
			executorService.submit(()-> {				
				bankAccount.credit(100);
			});
		});
		
		executorService.shutdown();
		executorService.awaitTermination(4000, TimeUnit.MILLISECONDS);
		
		System.out.println("Final Balance: " + bankAccount.getBalance());
	}
	
}