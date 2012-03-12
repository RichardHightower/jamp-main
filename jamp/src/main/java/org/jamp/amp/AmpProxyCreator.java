package org.jamp.amp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/** Creates a proxy object around an interface to expose method to send to remote hosts via STOMP, REST or websockets. 
 *  Takes a method encoder and an invoker.
 *  
 *  The invoker sends the message remotely.
 *  The encoder encodes the method via Hessian, JSON or BSON.
 **/
public class AmpProxyCreator {
	AmpMessageSender invoker;
    static int messageId = 0;

	
	public AmpProxyCreator (AmpMessageSender invoker) {
        this.invoker = invoker;
 	    
	}
	
	public Object createProxy(final String interface_, final String toURL, final String fromURL) throws Exception {
		return createProxy(Class.forName(interface_), toURL, fromURL);
	}
	
	public Object createProxy(final Class<?> interface_, final String toURL, final String fromURL) throws Exception {

		InvocationHandler handler = new InvocationHandler() {
			public Object invoke(Object arg0, Method method, Object[] params)
					throws Throwable {

			    AmpMessage.Type messageType = method.getReturnType().equals(Void.TYPE) ? AmpMessage.Type.SEND : AmpMessage.Type.QUERY;
				AmpMessage message = new AmpMessage(messageType, toURL, fromURL, method.getName(), params);
				messageId ++;
				message.setQueryId(System.currentTimeMillis() + "-" + messageId + "-"+ (int)(Math.random() * 100000));				
				invoker.sendMessage(message);
				return null;
			}
		};
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{interface_}, handler);
	}
	
}
