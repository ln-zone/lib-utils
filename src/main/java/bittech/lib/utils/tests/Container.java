package bittech.lib.utils.tests;

import bittech.lib.utils.Require;
import bittech.lib.utils.Try;
import bittech.lib.utils.exceptions.StoredException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;

public class Container implements AutoCloseable {
	
	private final GenericContainer<?> container;

	public Container(String dockerFilePath) {
		try {
			Require.notEmpty(dockerFilePath, "dockerFilePath");
			container = new GenericContainer<>(new ImageFromDockerfile().withDockerfile(Paths.get(dockerFilePath)));
			container.start();
		} catch (Exception ex) {
			throw new StoredException("Failed to start container from file: " + dockerFilePath, ex);
		}
	}

	public String getIp() {
		return container.getContainerInfo().getNetworkSettings().getNetworks().get("bridge").getIpAddress();
	}

	@Override
	public void close() {
		Try.printIfThrown(() -> container.close());
	}
}
