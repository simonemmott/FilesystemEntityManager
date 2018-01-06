package com.k2.FilesystemEntityManager;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.StringUtil;
import com.k2.Util.Identity.IdentityUtil;

public class FemTestClient extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public static enum Instruction {
		WAIT,
		COMMIT,
		ROLLBACK,
		FETCH,
		SAVE,
		DELETE,
		END;
	}
	
	public static enum State {
		STARTING,
		WAITING,
		PROCESSING,
		ENDING;
	}
	
	public static class Waiter {}
	
	public interface FemTestResult {
		String getMessage();
	}
	
	public static class Fault implements FemTestResult {
		private Throwable cause = null;
		public Throwable getCause() { return cause; }
		
		private String message = null;
		public String getMessage() { return (message != null) ? message : cause.getMessage(); }
		
		private Fault(String message) {
			this.message = message;
		}
		private Fault(Throwable cause) {
			this.cause = cause;
		}
		private Fault(String message, Throwable cause) {
			this.message = message;
			this.cause = cause;
		}
	}
	
	public static class Success implements FemTestResult {
		String message = null;
		Object result = null;
		
		private Success(String message) {
			logger.trace("Success: {}", message);
			this.message = message;
		}
		private Success(Object result) {
			logger.trace("Success: {}", StringUtil.toString(result));
			this.result = result;
		}
		private Success(String message, Object result) {
			logger.trace("Success: {}, {}", message, StringUtil.toString(result));
			this.message = message;
			this.result = result;
		}
		public String getMessage() { return message; }
		public Object getResult() { return result; }
	}

	private FilesystemEntityManager fem;
	private Waiter parentWaiter;
	private Waiter waiter;
	private String name;

	public FemTestClient(String name, Waiter waiter, FilesystemEntityManager fem) {
		this.name = name;
		this.parentWaiter = waiter;
		this.waiter = new Waiter();
		this.fem = fem;
	}
		
	public FilesystemEntityManager entityManager() { return fem; }
	
	public Waiter waiter() { return waiter; }
	
	private State state = State.STARTING;
	public State state() { return state; }
	
	private Object[] args = null;
	
	private Instruction instruction = Instruction.WAIT;
	private void instruct(Instruction instruction, Object ... args) {
		this.instruction = instruction;
		this.args = args;
		state = State.PROCESSING;
	}
	public Waiter fetch(Class<?> cls, Serializable key) {
		if (cls == null || key == null) return waiter;
		instruct(Instruction.FETCH, cls, key);
		return waiter;
	}
	public Waiter commit() {
		instruct(Instruction.COMMIT);
		return waiter;
	}
	public Waiter rollback() {
		instruct(Instruction.ROLLBACK);
		return waiter;
	}
	public Waiter save(Object obj) {
		if (obj == null) return waiter;
		instruct(Instruction.SAVE, obj);
		return waiter;
	}
	public Waiter delete(Object obj) {
		if (obj == null) return waiter;
		instruct(Instruction.DELETE, obj);
		return waiter;
	}
	public Waiter end() {
		instruct(Instruction.END);
		return waiter;
	}
	
	private FemTestResult result;
	public FemTestResult getResult() { return result; }
	
	private void setResult(FemTestResult result) {
		this.result = result;
		args = null;
		instruction = Instruction.WAIT;
	}

	private void setFinalResult(FemTestResult result) {
		this.result = result;
		args = null;
		instruction = null;
	}

	@Override
	public void run() {
		try {
			logger.info(name+": Starting thread");
			while (state != State.ENDING) {
				
				switch (instruction) {
				case COMMIT:
				{
					logger.trace(name+": Committing");
					try {
						fem.commit();
						setResult(new Success("Committed:"));
					} catch (Throwable cause) {
						Fault f = new Fault("Commit failure:", cause);
						logger.error(f.getMessage(), cause);
						setResult(f);
					}
				}
				break;
				case DELETE:
				{
					logger.debug(name+": Deleting");
					Object obj = args[0];
					try {
						setResult(new Success("Deleted: "+obj.getClass().getCanonicalName()+"("+IdentityUtil.getId(obj)+")", fem.delete(obj)));
					} catch (Throwable cause) {
						Fault f;
						try {
							f = new Fault("Delete failure for: "+obj.getClass().getCanonicalName()+"("+IdentityUtil.getId(obj)+")", cause);
							logger.error(f.getMessage(), cause);
							setResult(f);
						} catch (Throwable e) {
							f = new Fault("Delete failure for: "+obj.getClass().getCanonicalName(), cause);
							logger.error(f.getMessage(), cause);
							setResult(f);
						}
					}
				}
				break;
				case END:
					logger.debug(name+": Ending");
					state = State.ENDING;
					setFinalResult(new Success("Ending:"));
					synchronized(parentWaiter) { parentWaiter.notify(); }
					break;
				case FETCH:
				{
					logger.debug(name+": Fetching");
					Class<?> cls = (Class<?>) args[0];
					Serializable key = (Serializable) args[1];
					try {
						setResult(new Success("Fetched: "+cls.getCanonicalName()+"("+key+")", fem.fetch(cls, key)));
					} catch (Throwable cause) {
						Fault f = new Fault("Fetch failure for: "+cls.getCanonicalName()+"("+key+")", cause);
						logger.error(f.getMessage(), cause);
						setResult(f);
					}
				}
				break;
				case ROLLBACK:
				{
					logger.debug(name+": Rolling back");
					try {
						fem.rollback();
						setResult(new Success("Rolled back:"));
					} catch (Throwable cause) {
						Fault f = new Fault("Rollback failure:", cause);
						logger.error(f.getMessage(), cause);
						setResult(f);
					}
				}
				break;
				case SAVE:
				{
					logger.debug(name+": Saving");
					Object obj = args[0];
					try {
						setResult(new Success("Saved: "+obj.getClass().getCanonicalName()+"("+IdentityUtil.getId(obj)+")", fem.save(obj)));
					} catch (Throwable cause) {
						Fault f;
						try {
							f = new Fault("Save failure for: "+obj.getClass().getCanonicalName()+"("+IdentityUtil.getId(obj)+")", cause);
							logger.error(f.getMessage(), cause);
							setResult(f);
						} catch (Throwable e) {
							f = new Fault("Save failure for: "+obj.getClass().getCanonicalName(), cause);
							logger.error(f.getMessage(), cause);
							setResult(f);
						}
					}
				}
				break;
				case WAIT:
					logger.debug(name+": Waiting");
					state = State.WAITING;
					synchronized(parentWaiter) { parentWaiter.notify(); }
					synchronized(waiter) {
						try {
							waiter.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					result = null;
					break;
				}
			}
			logger.info(name+": Done");
		} finally {
			if (fem != null) {
				logger.info(name+": Closing filesystm entity manager");
				fem.close();
			}
		}
	}
	
}
