package no.mil.fnse.east_west;

import java.net.InetAddress;

public interface HttpController<E,T> {

	E put(InetAddress ip, String url, T request);
	
	E post(InetAddress ip, String url ,T request);
	
	E get(InetAddress ip, String url,T requestBody);

}
