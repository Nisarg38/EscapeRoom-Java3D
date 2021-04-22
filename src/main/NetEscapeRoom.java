package main;

// NetFourByFour.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A network version of FourByFour.java

  Message sent by client:
        -- initial opening of connetion
            * receive back     ok <id>   or   full
        -- try <posn>
            * may receive back tooFewPlayers
        -- disconnect

   Changes:
      - playerID is 1 or 2
      - altered Board so does not change playerID

      - removed GUI button (jbDisconnect) and two text fields
        for reporting ID and status
      - text fields replaced by overlays on the canvas
      - disconnect button idea not used
      - replaced Board's gameOver with isDisabled in NetFourByFour

    When a player (e.g. p1) wants to make a move:
       1. PickDragBehaviour calls tryMove() in this class.
       2. The PlayerServerHandler is informed with a "try <posn>" message
             - p1 may get back a "tooFewPlayers" message, or
             - the handler sends an "otherTurn <playerID><posn>" message 
               to the other player (p2)
       3. Without waiting for any response, tryMove calls doMove(), which
          calls tryPosn() in WrapNetFBF, and then tryPosn() in Board.
       4. Board updates the data structures, and calls set() in Positions
          to update the 3D representation.

    For the other player (p2):
       1. The FBFServer sends a "otherTurn <playerID><posn>"
          message to p2's FBFWatcher.
       2. FBFWatcher calls doMove() in its NetFourByFour object.
       3 & 4. Same as for p1 above

    When Board detects that a player has won:
       1. Board calls its reportWinner(), which then calls disable()
          in this object.
       2. disable() breaks the network connection, updates the status
          text , and sets isDiabled to true. isDisabled will prevent
          any more calls to tryMove() in NetFourByFour 
*/

import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class NetEscapeRoom extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int PORT = 4321;          // server details
	private static final String HOST = "localhost";

	private static final int MAX_PLAYERS = 2;
	private final static int PLAYER1 = 1;
	private final static int PLAYER2 = 2;

	private RotatingCube rotCube;
	private EscapeRoom eR;

	private Socket sock;
	private PrintWriter out;
	                          // game-related
	private int playerID;
	private String status;    // used to place info into the 3D canvas
	private int numPlayers;
	private int currPlayer;       // which player can take a turn now?
	private boolean isDisabled; // to indicate that the game has ended

	public NetEscapeRoom() {
		super("Net Four By Four");

		playerID = 0;                                   // no id as yet
		status = null;                           // no status to report
		numPlayers = 0;
		currPlayer = 1;                        // player 1 starts first
		isDisabled = false;

		makeContact();

		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		eR = new EscapeRoom(this, playerID);
		c.add(eR);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disable("exiting");
				System.exit(0);
			}
		});

		pack();
		setResizable(false); // fixed size display
		setVisible(true);
	}

	/* a function to contact the FBFServer and then one of its player handlers */
	private void makeContact() {
		try {
			sock = new Socket(HOST, PORT);
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true); // autoflush

			new FBFWatcher(this, in).start(); // start watching for server msgs
		} catch (Exception e) { // System.out.println(e);
			System.out.println("Cannot contact the NetFourByFour Server");
			System.exit(0);
		}
	}

	/* a function called by PickDragBehaviour to initiate a new move */
	/* Called by this object to execute this player's move, and by FBFWatcher to
	 * execute the other player's move. But the synchronized keyword means never at
	 * the same time.
	 */

	private int otherPlayer(int id)	{
		int otherID = ((id == PLAYER1) ? PLAYER2 : PLAYER1);
		return otherID;
	}

	/* Disconnect from network but do not terminate. No more moves will be allowed
	 * but the 3D image will be rotatable.
	 * 
	 * Called by: the close box, FBFWatcher, and Board (via gameWon()).
	 */

	/* Called from reportWinner() in Board to announce a win by player pid with
	 * score.
	 */
	public void gameWon(int pid) {
		if (pid == playerID) { // this client has won
			disable("You've won!");
			eR.youWon();
		}
		else
			disable("Player " + pid + " has won");
	}


	public void setStatus(String msg) {
		status = msg;
	}

	/* a function regularly called from OverlayCanvas, to update its display */
	public String getStatus()
	{
		return status;
	}

	// -------------------------------------------------
	// the following methods are only called from FBFWatcher

	// a function to add the other player
	public void addPlayer()	{
		numPlayers++;
		
	} 

	/* a function to remove the other player */

	public void disable(String msg) {
		if (!isDisabled) {                  // the client can only be disabled once
			try {
				isDisabled = true;
				out.println("disconnect"); // tell server
				sock.close();
				setStatus("Game Over: " + msg); 
				// System.out.println("Disabled: " + msg);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	public void removePlayer() {
		numPlayers--;
		if (numPlayers < MAX_PLAYERS)
			disable("Player " + otherPlayer(playerID) + " has won"); // disable client
	}

	/* This player is given a playerID. Update the window's title bar. The id is
	 * also the number of current players, so can be used to check whether the game
	 * can start.
	 */
	public void setPlayerID(int id) {
		System.out.println("My player ID: " + id);
		playerID = id;
		if (playerID == PLAYER1)
			setTitle("Rotating Cube. Player 1");
		else // PLAYER2
			setTitle("Rotating Cube. Player 2");

		numPlayers = id;
		
	} // end of setPlayerID()

   // **************************************

	public static void main(String[] args) {
		new NetEscapeRoom();
	}
}
