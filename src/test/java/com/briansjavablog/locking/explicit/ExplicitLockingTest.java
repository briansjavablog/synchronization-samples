package com.briansjavablog.locking.explicit;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.Test;

public class ExplicitLockingTest {
	
	
	@Test
	public void testReentrantLockBankAccount() throws InterruptedException{
		
		ReentrantLockBankAccount bankAccount = new ReentrantLockBankAccount();
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		IntStream.rangeClosed(1, 1000).forEach(i->{			
			executorService.submit(()-> {			
				try {
					bankAccount.debit(100);
				} catch (Exception ex) {
					ex.printStackTrace();
				}			
			});
			executorService.submit(()-> {				
				bankAccount.credit(100);
			});
		});
		
		executorService.shutdown();
		executorService.awaitTermination(4000, TimeUnit.MILLISECONDS);
		
		System.out.println("Final Balance: " + bankAccount.getBalance());
	}
	
	
	@Test
	public void testReentrantReadWriteLockTransactionLog() throws InterruptedException{
		
		AtomicInteger transactionCount = new AtomicInteger();
		ReadWriteReentrantLockTransactionLog transactionLog = new ReadWriteReentrantLockTransactionLog();
		
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
		
		Instant tenSecondsTime = Instant.now().plusSeconds(2);
		
		while(Instant.now().isBefore(tenSecondsTime)){
			
			executorService.scheduleAtFixedRate(()-> {			
				try {
					transactionCount.incrementAndGet();
					transactionLog.saveTransaction("Debit: " + 100);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}, 0, 1000, TimeUnit.MILLISECONDS);
			
			executorService.scheduleAtFixedRate(()-> {			
				try {
					transactionCount.incrementAndGet();
					transactionLog.saveTransaction("Credit: " + 100);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}, 0, 1000, TimeUnit.MILLISECONDS);	
			
			executorService.scheduleAtFixedRate(()-> {				
				System.out.println("Transaction Count: " + transactionLog.getTransactions().size());
			}, 0, 500, TimeUnit.MILLISECONDS);
		}
		
		executorService.shutdown();
		executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
		
		System.out.println("Total Transactions Submitted: " + transactionCount);
		System.out.println("Total Transactions Logged: " + transactionLog.getTransactions().size());
	}	
}