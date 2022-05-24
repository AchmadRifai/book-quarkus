package achmad.rifai.book.quarkus.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class TimestampCapturer {

	@Inject Logger logger;

	public long now() {
		return System.currentTimeMillis();
	}

}
