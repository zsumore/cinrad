package mq.radar.cinrad.util;

public interface CancelTask {

	/**
	 * Called routine should check often during the task and cancel the task if
	 * it returns true.
	 * 
	 * @return true if task was cancelled
	 */
	public boolean isCancel();

	/**
	 * Called routine got an error, so it sets a message for calling program to
	 * show to user.
	 * 
	 * @param msg
	 *            message to show user
	 */
	public void setError(String msg);
}