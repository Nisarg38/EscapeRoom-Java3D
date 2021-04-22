package main;

// FBFWatcher.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* FBFWatcher monitors the stream coming from the server
   which will contain messages that must be processed by 
   the client (a NetFourByFour object)

   Incoming Messages:
    ok <playerID>           -- connection accepted; include player ID
    full                    -- connection refused; server has enough players
    tooFewPlayers           -- turn rejected, since not enough players
    otherTurn <player> <posn>  -- turn by other player sent to client
    added <player>          -- other player added to server
    removed <player>        -- other player removed
*/

import java.io.*;
import java.util.*;

public class FBFWatcher extends Thread {
	private NetEscapeRoom fbf;                    // ref back to client
	private BufferedReader in;

	public FBFWatcher(NetEscapeRoom fbf, BufferedReader i) {
		this.fbf = fbf;
		in = i;
	}

	/* a function to read server messages and act on them */
	public void run() {
		String line;
		try {
			while ((line = in.readLine()) != null) {
				if (line.startsWith("ok"))
					extractID(line.substring(3));
				else if(fbf.getStatus() == "Winner!") {
					fbf.disable("Winner!");
				}
				else if (line.startsWith("full"))
					fbf.disable("full game");             // disable client
				else if (line.startsWith("tooFewPlayers"))
					fbf.disable("other player has left"); // disable client	
				else if (line.startsWith("added"))          // don't use ID
					fbf.addPlayer();            // client adds other player
				else if (line.startsWith("removed"))        // don't use ID
					fbf.removePlayer();       // client removes other player
				else // anything else
					System.out.println("ERR: " + line + "\n");
			}
		} catch (Exception e) // socket closure will cause termination of while
		{
			fbf.disable("server link lost"); // end game as well
		}
	}

	private void extractID(String line) {         // line format: <player id>
		StringTokenizer tokens = new StringTokenizer(line);
		try {
			int id = Integer.parseInt(tokens.nextToken());
			fbf.setPlayerID(id); // client gets its playerID
		} catch (NumberFormatException e) {
			System.out.println(e);
		}
	}
	
	private void reportWinner(int playerID) {

		fbf.gameWon(playerID);
	}

} 
