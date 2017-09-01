package com.smartlogic.ontologyeditor;

import java.io.IOException;

public class AddListener extends ModelManipulation {

	public static void main(String[] args) throws IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		oeClient.addListener("http://com.smartlogic.playpen/Listener#66", "http://localhost:8090/update");
	}


}
