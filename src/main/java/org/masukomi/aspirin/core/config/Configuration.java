/*
 * Created on Jan 5, 2004
 * 
 * Copyright (c) 2004 Katherine Rhodes (masukomi at masukomi dot org)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.masukomi.aspirin.core.config;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.ParseException;

import org.masukomi.aspirin.core.AspirinInternal;
import org.masukomi.aspirin.core.store.mail.MailStore;
import org.masukomi.aspirin.core.store.mail.SimpleMailStore;
import org.masukomi.aspirin.core.store.queue.QueueStore;
import org.masukomi.aspirin.core.store.queue.SimpleQueueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>This class represents the configuration of Aspirin. You can configure this 
 * software two ways:</p>
 * 
 * <ol>
 *   <li>Get the configuration instance and set parameters.</li>
 *   <li>Get the instance and initialize with a Properties object.</li>
 * </ol>
 * 
 * <p>There is a way to change behavior of Aspirin dinamically. You can use 
 * JMX to change configuration parameters. In the parameters list we marked the 
 * parameters which are applied immediately. For more informations view 
 * {@link ConfigurationMBean}.</p>
 * 
 * TODO Use map to store configuration items and values, this will help us to 
 * store special settings for QoS implementations.
 * 
 * <table border="1">
 *   <tr>
 *     <th>Name</th>
 *     <th>Deprecated name</th>
 *     <th>Type</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>aspirin.delivery.attempt.delay</td>
 *     <td>aspirinRetryInterval</td>
 *     <td>Integer</td>
 *     <td>The delay of next attempt to delivery in milliseconds. <i>Change by 
 *     JMX applied immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.delivery.attempt.count</td>
 *     <td>aspirinMaxAttempts</td>
 *     <td>Integer</td>
 *     <td>Maximal number of delivery attempts of an email. <i>Change by JMX 
 *     applied immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.delivery.bounce-on-failure</td>
 *     <td></td>
 *     <td>Boolean</td>
 *     <td>If true, a bounce email will be send to postmaster on failure. 
 *     <i>Change by JMX applied immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.delivery.debug</td>
 *     <td></td>
 *     <td>Boolean</td>
 *     <td>If true, full SMTP communication will be logged. <i>Change by JMX 
 *     applied immediately.</i></td>
 *   </tr>
 *   <tr>
 *   	<td>aspirin.delivery.expiry</td>
 *   	<td></td>
 *   	<td>Long</td>
 *   	<td>Time of sending expiry in milliseconds. The queue send an email 
 *   	until current time = queueing time + expiry. Default value is -1, it 
 *   	means forever (no expiration time). <i>Change by JMX applied 
 *   	immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.delivery.threads.active.max</td>
 *     <td>aspirinDeliverThreads</td>
 *     <td>Integer</td>
 *     <td>Maximum number of active delivery threads in the pool. <i>Change by 
 *     JMX applied immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.delivery.threads.idle.max</td>
 *     <td>aspirinDeliverThreads</td>
 *     <td>Integer</td>
 *     <td>Maximum number of idle delivery threads in the pool (the deilvery 
 *     threads over this limit will be shutdown). <i>Change by JMX applied 
 *     immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.delivery.timeout</td>
 *     <td></td>
 *     <td>Integer</td>
 *     <td>Socket and {@link Transport} timeout in milliseconds. <i>Change by 
 *     JMX applied immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.encoding</td>
 *     <td></td>
 *     <td>String</td>
 *     <td>The MIME encoding. <i>Change by JMX applied immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.hostname</td>
 *     <td>aspirinHostname</td>
 *     <td>String</td>
 *     <td>The hostname. <i>Change by JMX applied immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.logger.name</td>
 *     <td></td>
 *     <td>String</td>
 *     <td>
 *       The name of the logger. <i>Change by JMX applied immediately.</i>
 *       <br/>
 *       <strong>WARNING! Changing logger name cause replacing of logger.</strong>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.logger.prefix</td>
 *     <td></td>
 *     <td>String</td>
 *     <td>The prefix of the logger. This will be put in the logs at the first 
 *     position. <i>Change by JMX applied immediately.</i></td>
 *   </tr>
 *   <tr>
 *     <td>aspirin.postmaster.email</td>
 *     <td>aspirinPostmaster</td>
 *     <td>String</td>
 *     <td>The email address of the postmaster. <i>Change by JMX applied 
 *     immediately.</i></td>
 *   </tr>
 *   <tr>
 *   	<td>aspirin.mailstore.class</td>
 *   	<td></td>
 *   	<td>String</td>
 *   	<td>The class name of mail store. Default class is SimpleMailStore in 
 *   	org.masukomi.aspirin.core.store package.</td>
 *   </tr>
 *   <tr>
 *   	<td>aspirin.queuestore.class</td>
 *   	<td></td>
 *   	<td>String</td>
 *   	<td>The class name of queue store. Default class is SimpleQueueStore in 
 *   	org.masukomi.aspirin.core.queue package.</td>
 *   </tr>
 * </table>
 * 
 * @author Kate Rhodes masukomi at masukomi dot org
 * @author Laszlo Solova
 */
public class Configuration implements ConfigurationMBean {
	
	private static Configuration instance;
	private int maxAttempts = 3; // aspirin.delivery.attempt.count
	private long retryInterval = 300000; // aspirin.delivery.attempt.delay
	private boolean bounceOnFailure = true; //aspirin.delivery.bounce-on-failure
	private boolean debugCommunication = false; // aspirin.delivery.debug
	private String hostname = "localhost"; // aspirin.delivery.hostname
	private int deliveryThreads = 3; // aspirin.delivery.threads.active.max
	private int idleDeliveryThreads = deliveryThreads; // aspirin.delivery.threads.idle.max
	private int connectionTimeout = 30000; // in milliseconds, aspirin.delivery.timeout
	private String encoding = "UTF-8"; // aspirin.encoding
	private long expiry = -1; // aspirin.delivery.expiry
	private static String loggerName = "Aspirin"; // aspirin.logger.name
	private static Logger log = LoggerFactory.getLogger(loggerName); // inherited from aspirin.logger.name
	private String loggerPrefix = "Aspirin "; // aspirin.logger.prefix
	private MailStore mailStore = null;
	private String mailStoreClassName = SimpleMailStore.class.getCanonicalName(); // aspirin.mailstore.class
	private QueueStore queueStore = null;
	private String queueStoreClassName = SimpleQueueStore.class.getCanonicalName(); // aspirin.queuestore.class
	protected InternetAddress postmaster = null; // inherited from aspirin.postmaster.email
	private Session mailSession = null;
	
	private List<ConfigurationChangeListener> listeners;

	static public Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	public void init(Properties props) {
		String tempString = null;
		
		tempString = props.getProperty(PARAM_DELIVERY_ATTEMPT_DELAY);
		if( tempString != null )
		{
			retryInterval = Long.valueOf(tempString);
		}else
		{
			// We need this to support backward compatibility
			tempString = System.getProperty("aspirinRetryInterval");
			if( tempString != null )
			{
				retryInterval = Long.valueOf(tempString);
			}else
			{
				tempString = System.getProperty(PARAM_DELIVERY_ATTEMPT_DELAY);
				if( tempString != null )
				{
					retryInterval = Long.valueOf(tempString);
				}
			}
		}
		
		tempString = props.getProperty(PARAM_DELIVERY_ATTEMPT_COUNT);
		if( tempString != null )
		{
			maxAttempts = Integer.valueOf(tempString);
		}else
		{
			// We need this to support backward compatibility
			tempString = System.getProperty("aspirinMaxAttempts");
			if( tempString != null )
			{
				maxAttempts = Integer.valueOf(tempString);
			}else
			{
				tempString = System.getProperty(PARAM_DELIVERY_ATTEMPT_COUNT);
				if( tempString != null )
				{
					maxAttempts = Integer.valueOf(tempString);
				}
			}
		}
		
		tempString = props.getProperty(PARAM_DELIVERY_DEBUG);
		if( tempString != null )
			debugCommunication = ("true".equalsIgnoreCase(tempString) ) ? true : false;
		
		tempString = props.getProperty(PARAM_DELIVERY_THREADS_ACTIVE_MAX);
		if( tempString != null )
		{
			deliveryThreads = Integer.valueOf(tempString);
		}else
		{
			// We need this to support backward compatibility
			tempString = System.getProperty("aspirinDeliverThreads");
			if( tempString != null )
			{
				deliveryThreads = Integer.valueOf(tempString);
			}else
			{
				tempString = System.getProperty(PARAM_DELIVERY_THREADS_ACTIVE_MAX);
				if( tempString != null )
				{
					deliveryThreads = Integer.valueOf(tempString);
				}
			}
		}
		
		tempString = props.getProperty(PARAM_DELIVERY_THREADS_IDLE_MAX);
		if( tempString != null )
		{
			idleDeliveryThreads = Integer.valueOf(tempString);
		}else
		{
			// We need this to support backward compatibility
			tempString = System.getProperty("aspirinDeliverThreads");
			if( tempString != null )
			{
				idleDeliveryThreads = Integer.valueOf(tempString);
			}else
			{
				tempString = System.getProperty(PARAM_DELIVERY_THREADS_IDLE_MAX);
				if( tempString != null )
				{
					idleDeliveryThreads = Integer.valueOf(tempString);
				}
			}
		}
		
		tempString = props.getProperty(PARAM_DELIVERY_TIMEOUT);
		if( tempString != null )
			connectionTimeout = Integer.valueOf(tempString);
		
		tempString = props.getProperty(PARAM_POSTMASTER_EMAIL);
		if( tempString != null )
		{
			setPostmasterEmail(tempString);
		}else
		{
			tempString = System.getProperty("aspirinPostmaster");
			if( tempString != null )
			{
				setPostmasterEmail(tempString);
			}else
			{
				tempString = System.getProperty(PARAM_POSTMASTER_EMAIL);
				if( tempString != null )
				{
					setPostmasterEmail(tempString);
				}
			}
		}
		
		hostname = props.getProperty(
				PARAM_HOSTNAME, 
				System.getProperty("aspirinHostname", 
						System.getProperty("mail.smtp.host",
								System.getProperty(PARAM_HOSTNAME, hostname)
						)
				)
		);
		
		encoding = props.getProperty(PARAM_ENCODING, encoding);
		
		String expiryString = props.getProperty(PARAM_DELIVERY_EXPIRY);
		if( expiryString != null )
			expiry = Long.valueOf(expiryString);
		
		String loggerConfigName = props.getProperty(PARAM_LOGGER_NAME);
		if( loggerConfigName != null && !loggerConfigName.equals(loggerName) )
			log = LoggerFactory.getLogger(loggerName);
		loggerPrefix = props.getProperty(PARAM_LOGGER_PREFIX, loggerPrefix);
		
		mailStoreClassName = props.getProperty(PARAM_MAILSTORE_CLASS, mailStoreClassName);
		
		queueStoreClassName = props.getProperty(PARAM_QUEUESTORE_CLASS, queueStoreClassName);
		
		updateMailSession();
	}
	
	/**
	 *  
	 */
	Configuration() {
		init(new Properties());
	}
	/**
	 * @return The email address of the postmaster in a MailAddress object.
	 */
	public InternetAddress getPostmaster() {
		return postmaster;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
		updateMailSession();
		notifyListeners(PARAM_HOSTNAME);
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
		updateMailSession();
		notifyListeners(PARAM_ENCODING);
	}

	@Override
	public int getDeliveryAttemptCount() {
		return maxAttempts;
	}

	@Override
	public int getDeliveryAttemptDelay() {
		return (int)retryInterval;
	}

	@Override
	public int getDeliveryThreadsActiveMax() {
		return deliveryThreads;
	}
	
	@Override
	public int getDeliveryThreadsIdleMax() {
		return idleDeliveryThreads;
	}

	@Override
	public int getDeliveryTimeout() {
		return connectionTimeout;
	}
	
	@Override
	public long getExpiry() {
		return expiry;
	}

	@Override
	public String getLoggerName() {
		return loggerName;
	}

	@Override
	public String getLoggerPrefix() {
		return loggerPrefix;
	}
	
	public MailStore getMailStore() {
		if( mailStore == null )
		{
			try {
				Class<?> storeClass = (Class<?>) Class.forName(mailStoreClassName);
				if( storeClass.getInterfaces()[0].equals(MailStore.class) )
					mailStore = (MailStore)storeClass.newInstance();
			} catch (Exception e) {
				log.error(getClass().getSimpleName()+" Mail store class could not be instantiated. Class="+mailStoreClassName, e);
				mailStore = new SimpleMailStore();
			}
		}
		return mailStore;
	}
	
	@Override
	public String getPostmasterEmail() {
		return postmaster.toString();
	}
	
	public QueueStore getQueueStore() {
		if( queueStore == null )
		{
			try {
				Class<?> storeClass = (Class<?>) Class.forName(queueStoreClassName);
				if( storeClass.getInterfaces()[0].equals(QueueStore.class) )
					queueStore = (QueueStore)storeClass.newInstance();
			} catch (Exception e) {
				log.error(getClass().getSimpleName()+" Queue store class could not be instantiated. Class="+queueStoreClassName, e);
				queueStore = new SimpleQueueStore();
			}
		}
		return queueStore;
	}
	
	@Override
	public boolean isDeliveryBounceOnFailure() {
		return bounceOnFailure;
	}

	@Override
	public boolean isDeliveryDebug() {
		return debugCommunication;
	}

	@Override
	public void setDeliveryAttemptCount(int attemptCount) {
		this.maxAttempts = attemptCount;
		notifyListeners(PARAM_DELIVERY_ATTEMPT_COUNT);
	}

	@Override
	public void setDeliveryAttemptDelay(int delay) {
		this.retryInterval = delay;
		notifyListeners(PARAM_DELIVERY_ATTEMPT_DELAY);
	}
	
	@Override
	public void setDeliveryBounceOnFailure(boolean bounce) {
		this.bounceOnFailure = bounce;
		notifyListeners(PARAM_DELIVERY_BOUNCE_ON_FAILURE);
	}

	@Override
	public void setDeliveryDebug(boolean debug) {
		this.debugCommunication = debug;
		updateMailSession();
		notifyListeners(PARAM_DELIVERY_DEBUG);
	}

	@Override
	public void setDeliveryThreadsActiveMax(int activeThreadsMax) {
		this.deliveryThreads = activeThreadsMax;
		notifyListeners(PARAM_DELIVERY_THREADS_ACTIVE_MAX);
	}
	
	@Override
	public void setDeliveryThreadsIdleMax(int idleThreadsMax) {
		this.idleDeliveryThreads = idleThreadsMax;
		notifyListeners(PARAM_DELIVERY_THREADS_IDLE_MAX);
	}

	@Override
	public void setDeliveryTimeout(int timeout) {
		this.connectionTimeout = timeout;
		updateMailSession();
		notifyListeners(PARAM_DELIVERY_TIMEOUT);
	}
	
	@Override
	public void setExpiry(long expiry) {
		this.expiry = expiry;
		notifyListeners(PARAM_DELIVERY_EXPIRY);
	}

	@Override
	public void setLoggerName(String loggerName) {
		Configuration.loggerName = loggerName;
		log = LoggerFactory.getLogger(loggerName);
		notifyListeners(PARAM_LOGGER_NAME);
	}

	@Override
	public void setLoggerPrefix(String loggerPrefix) {
		this.loggerPrefix = loggerPrefix;
		notifyListeners(PARAM_LOGGER_PREFIX);
	}
	
	public void setMailStore(MailStore mailStore) {
		this.mailStore = mailStore;
	}

	@Override
	public void setPostmasterEmail(String emailAddress) {
		if( emailAddress == null )
		{
			this.postmaster = null;
			return;
		}
		try
		{
			this.postmaster = new InternetAddress(emailAddress);
			notifyListeners(PARAM_POSTMASTER_EMAIL);
		}catch (ParseException e)
		{
			log.error(getClass().getSimpleName()+".setPostmasterEmail(): The email address is unparseable.", e);
		}
	}
	
	public void setQueueStore(QueueStore queueStore) {
		this.queueStore = queueStore;
	}
	
	public void addListener(ConfigurationChangeListener listener) {
		if( listeners == null )
			listeners = new ArrayList<ConfigurationChangeListener>();
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(ConfigurationChangeListener listener) {
		if( listeners != null )
		{
			synchronized (listeners) {
				listeners.remove(listener);
			}
		}
	}
	
	private void notifyListeners(String changedParameterName) {
		if( listeners != null && 0 < listeners.size() )
		{
			if( log.isInfoEnabled() )
				log.info(getClass().getSimpleName()+".notifyListeners(): Configuration parameter '"+changedParameterName+"' changed.");
			synchronized (listeners) {
				for( ConfigurationChangeListener listener : listeners )
					listener.configChanged(changedParameterName);
			}
		}
	}

	@Override
	public String getMailStoreClassName() {
		return mailStoreClassName;
	}

	@Override
	public void setMailStoreClassName(String className) {
		this.mailStoreClassName = className;
	}
	
	@Override
	public String getQueueStoreClassName() {
		return queueStoreClassName;
	}
	
	@Override
	public void setQueueStoreClassName(String className) {
		this.queueStoreClassName = className;
	}
	
	public Logger getLogger() {
		return LoggerFactory.getLogger(loggerName);
	}
	
	public Session getMailSession() {
		/**
		 * TODO check thread safe mode
		 */
		return mailSession;
	}
	
	private static final String MAIL_MIME_CHARSET = "mail.mime.charset";
	private static final String MAIL_SMTP_CONNECTIONTIMEOUT = "mail.smtp.connectiontimeout";
	private static final String MAIL_SMTP_HOST = "mail.smtp.host";
	private static final String MAIL_SMTP_LOCALHOST = "mail.smtp.localhost";
	private static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
	
	private void updateMailSession() {
		// Set up default session
		Properties mailSessionProps = System.getProperties();
		mailSessionProps.put(MAIL_SMTP_HOST, getHostname()); //The SMTP server to connect to.
		mailSessionProps.put(MAIL_SMTP_LOCALHOST, getHostname()); //Local host name. Defaults to InetAddress.getLocalHost().getHostName(). Should not normally need to be set if your JDK and your name service are configured properly.
		mailSessionProps.put(MAIL_MIME_CHARSET, getEncoding()); //The mail.mime.charset System property can be used to specify the default MIME charset to use for encoded words and text parts that don't otherwise specify a charset. Normally, the default MIME charset is derived from the default Java charset, as specified in the file.encoding System property. Most applications will have no need to explicitly set the default MIME charset. In cases where the default MIME charset to be used for mail messages is different than the charset used for files stored on the system, this property should be set.
		mailSessionProps.put(MAIL_SMTP_CONNECTIONTIMEOUT, getDeliveryTimeout()); //Socket connection timeout value in milliseconds. Default is infinite timeout.
		mailSessionProps.put(MAIL_SMTP_TIMEOUT, getDeliveryTimeout()); //Socket I/O timeout value in milliseconds. Default is infinite timeout.
		Session newSession = Session.getInstance(mailSessionProps);
		
		// Set communication debug
		if( ( AspirinInternal.getLogger() == null || AspirinInternal.getLogger().isDebugEnabled() ) && isDeliveryDebug() )
			newSession.setDebug(true);
		
		mailSession = newSession;
	}

}