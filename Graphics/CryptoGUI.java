import java.awt.Color;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class CryptoGUI extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				CryptoGUI frame = new CryptoGUI();
				frame.setVisible(true);
				frame.setResizable(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public final int FPS = 60;
	JTextArea textAreaPublicKey;
	JTextArea textAreaPrivateKey;
	JTextArea textAreaEncrypted;
	private String publicKey;
	private String privateKey;
	private String encryptedMessage;
	private String decryptedMessage;
	private String message;

	/**
	 * Create the frame.
	 * 
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public CryptoGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException, InterruptedException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		getContentPane().setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		getContentPane().setLayout(null);
		menuScene();

	}

	private void menuScene() {
		getContentPane().removeAll();
		getContentPane().repaint();
		// Header panel
		JPanel panel = new JPanel();
		panel.setBounds(-11, 0, 805, 66);
		panel.setBackground(new Color(3, 169, 244));
		getContentPane().add(panel);

		// Header label
		JLabel lblCryptoUtility = new JLabel("Crypto Utility");
		panel.add(lblCryptoUtility);
		lblCryptoUtility.setFont(new Font("Roboto Light", Font.PLAIN, 40));
		lblCryptoUtility.setHorizontalAlignment(SwingConstants.CENTER);

		// Page title
		JLabel lblDecryption = new JLabel("Menu");
		lblDecryption.setHorizontalAlignment(SwingConstants.CENTER);
		lblDecryption.setFont(new Font("Roboto Thin", Font.PLAIN, 30));
		lblDecryption.setBounds(280, 86, 222, 40);
		getContentPane().add(lblDecryption);

		// Buttons

		// Encrypt button
		JButton btnEncrypt = new JButton("Encrypt");
		btnEncrypt.setFont(new Font("Roboto", Font.PLAIN, 20));
		btnEncrypt.setBounds(115, 221, 138, 60);
		btnEncrypt.setFocusable(false);
		btnEncrypt.addActionListener(e -> {
			encryptScene();
		});
		getContentPane().add(btnEncrypt);

		// Decrypt button
		JButton btnDecrypt = new JButton("Decrypt");
		btnDecrypt.setFont(new Font("Roboto", Font.PLAIN, 20));
		btnDecrypt.setBounds(322, 221, 138, 60);
		btnDecrypt.setFocusable(false);
		btnDecrypt.addActionListener(e -> {
			decryptScene();
		});
		getContentPane().add(btnDecrypt);

		// Keygen button
		JButton btnKeygen = new JButton("Keygen");
		btnKeygen.setFont(new Font("Roboto", Font.PLAIN, 20));
		btnKeygen.setBounds(529, 221, 138, 60);
		btnKeygen.setFocusable(false);
		btnKeygen.addActionListener(e -> {
			keyGenScene();
		});
		getContentPane().add(btnKeygen);

		// Exit button
		JButton btnBack = new JButton("Exit");
		btnBack.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnBack.setBounds(593, 336, 112, 56);
		btnBack.addActionListener(e -> {
			System.exit(0);
		});
		getContentPane().add(btnBack);
	}

	private void encryptScene() {
		getContentPane().removeAll();
		getContentPane().repaint();
		// Header panel
		JPanel panel = new JPanel();
		panel.setBounds(-11, 0, 805, 66);
		panel.setBackground(new Color(3, 169, 244));
		getContentPane().add(panel);

		// Header label
		JLabel lblCryptoUtility = new JLabel("Crypto Utility");
		panel.add(lblCryptoUtility);
		lblCryptoUtility.setFont(new Font("Roboto Light", Font.PLAIN, 40));
		lblCryptoUtility.setHorizontalAlignment(SwingConstants.CENTER);

		// Page title
		JLabel lblEncryption = new JLabel("Encryption");
		lblEncryption.setHorizontalAlignment(SwingConstants.CENTER);
		lblEncryption.setFont(new Font("Roboto Thin", Font.PLAIN, 30));
		lblEncryption.setBounds(280, 86, 222, 40);
		getContentPane().add(lblEncryption);
		enableDragAndDropPrivateKey();

		// Text area border
		Border border = BorderFactory.createLineBorder(new Color(3, 169, 244), 2, true);

		// Encrypted text area
		JTextArea textAreaMessage = new JTextArea("");
		textAreaMessage.setFont(new Font("Roboto", Font.PLAIN, 18));
		textAreaMessage.setBackground(Color.WHITE);
		textAreaMessage.setBounds(158, 154, 192, 116);
		textAreaMessage.setLineWrap(true);
		textAreaMessage.setWrapStyleWord(true);
		textAreaMessage.setBorder(border);
		textAreaMessage.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
			}

			@Override
			public void focusGained(FocusEvent e) {
				textAreaMessage.setBorder(border);
			}
		});
		getContentPane().requestFocus();
		getContentPane().add(textAreaMessage);
		enableDragAndDropEncrypted();

		// Public key text area
		textAreaPublicKey = new JTextArea();
		textAreaPublicKey.setFont(new Font("Arial", Font.PLAIN, 15));
		textAreaPublicKey.setText("Drag public key here");
		textAreaPublicKey.setBackground(Color.WHITE);
		textAreaPublicKey.setBounds(430, 154, 189, 116);
		getContentPane().add(textAreaPublicKey);
		textAreaPublicKey.setFocusable(false);
		textAreaPublicKey.setBorder(border);
		enableDragAndDropPublicKey();

		// Error label
		JLabel lblError = new JLabel("Error occured");
		lblError.setForeground(Color.RED);
		lblError.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblError.setHorizontalAlignment(SwingConstants.CENTER);
		lblError.setBounds(266, 413, 250, 27);
		lblError.setVisible(false);
		getContentPane().add(lblError);

		// Decrypt button
		JButton btnEncrypt = new JButton("Encrypt");
		btnEncrypt.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnEncrypt.setBounds(298, 336, 192, 56);
		btnEncrypt.addActionListener(e -> {
			message = textAreaMessage.getText();
			if (message.equals("") || publicKey == null) {
				if (message.equals("")) {
					textAreaMessage.setBorder(BorderFactory.createLineBorder(Color.RED, 2, true));
				}

				if (publicKey == null) {
					textAreaPublicKey.setBorder(BorderFactory.createLineBorder(Color.RED, 2, true));
				}
			} else {
				try {
					message = "" + CryptographicAlgorithm.handleEncrypt(message, publicKey);
					lblError.setVisible(false);
					encryptedScene();
				} catch(ArrayIndexOutOfBoundsException e1) {
					lblError.setVisible(true);
					lblError.setText("Error. Message too long");
				}
				catch (Exception e1) {
					System.out.println("Error occured");
					lblError.setVisible(true);
					e1.printStackTrace();
				}
			}
		});
		getContentPane().add(btnEncrypt);

		// Back button
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnBack.setBounds(593, 336, 112, 56);
		btnBack.addActionListener(e -> {
			publicKey = null;
			privateKey = null;
			encryptedMessage = null;
			decryptedMessage = null;
			message = null;
			menuScene();
		});
		getContentPane().add(btnBack);

		// Enter here label
		JLabel lblEnterMessageHere = new JLabel("Enter Message here");
		lblEnterMessageHere.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnterMessageHere.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblEnterMessageHere.setBounds(158, 267, 192, 40);
		getContentPane().add(lblEnterMessageHere);
	}

	private void encryptedScene() {
		getContentPane().removeAll();
		getContentPane().repaint();
		// Header panel
		JPanel panel = new JPanel();
		panel.setBounds(-11, 0, 805, 66);
		panel.setBackground(new Color(3, 169, 244));
		getContentPane().add(panel);

		// Header label
		JLabel lblCryptoUtility = new JLabel("Crypto Utility");
		panel.add(lblCryptoUtility);
		lblCryptoUtility.setFont(new Font("Roboto Light", Font.PLAIN, 40));
		lblCryptoUtility.setHorizontalAlignment(SwingConstants.CENTER);

		// Page title
		JLabel lblEncryption = new JLabel("Result");
		lblEncryption.setHorizontalAlignment(SwingConstants.CENTER);
		lblEncryption.setFont(new Font("Roboto Thin", Font.PLAIN, 30));
		lblEncryption.setBounds(280, 86, 222, 40);
		getContentPane().add(lblEncryption);
		enableDragAndDropPrivateKey();

		// Back button
		JButton btnBack = new JButton("Menu");
		btnBack.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnBack.setBounds(593, 336, 112, 56);
		btnBack.addActionListener(e -> {
			publicKey = null;
			privateKey = null;
			encryptedMessage = null;
			decryptedMessage = null;
			message = null;
			menuScene();
		});
		getContentPane().add(btnBack);
		
		JLabel lblEncryptedMessageSaved = new JLabel("Encrypted message saved at \"encrypted.txt\"");
		lblEncryptedMessageSaved.setFont(new Font("Roboto", Font.PLAIN, 30));
		lblEncryptedMessageSaved.setBounds(93, 179, 596, 104);
		getContentPane().add(lblEncryptedMessageSaved);

	}

	private void decryptScene() {

		getContentPane().removeAll();
		getContentPane().repaint();
		
		// Header panel
		JPanel panel = new JPanel();
		panel.setBounds(-11, 0, 805, 66);
		panel.setBackground(new Color(3, 169, 244));
		getContentPane().add(panel);

		// Header label
		JLabel lblCryptoUtility = new JLabel("Crypto Utility");
		panel.add(lblCryptoUtility);
		lblCryptoUtility.setFont(new Font("Roboto Light", Font.PLAIN, 40));
		lblCryptoUtility.setHorizontalAlignment(SwingConstants.CENTER);

		// Page title
		JLabel lblDecryption = new JLabel("Decryption");
		lblDecryption.setHorizontalAlignment(SwingConstants.CENTER);
		lblDecryption.setFont(new Font("Roboto Thin", Font.PLAIN, 30));
		lblDecryption.setBounds(280, 86, 222, 40);
		getContentPane().add(lblDecryption);
		enableDragAndDropPrivateKey();

		// Text area border
		Border border = BorderFactory.createLineBorder(new Color(3, 169, 244), 2, true);

		// Encrypted text area
		textAreaEncrypted = new JTextArea();
		textAreaEncrypted.setText("Drag encrypted \nmessage here");
		textAreaEncrypted.setFont(new Font("Arial", Font.PLAIN, 15));
		textAreaEncrypted.setBackground(Color.WHITE);
		textAreaEncrypted.setBounds(158, 154, 192, 116);
		textAreaEncrypted.setLineWrap(true);
		textAreaEncrypted.setWrapStyleWord(true);
		getContentPane().add(textAreaEncrypted);
		textAreaEncrypted.setFocusable(false);
		textAreaEncrypted.setBorder(border);
		enableDragAndDropEncrypted();

		// Private key text area
		textAreaPrivateKey = new JTextArea();
		textAreaPrivateKey.setFont(new Font("Arial", Font.PLAIN, 15));
		textAreaPrivateKey.setText("Drag private key here");
		textAreaPrivateKey.setBackground(Color.WHITE);
		textAreaPrivateKey.setBounds(450, 154, 192, 116);
		getContentPane().add(textAreaPrivateKey);
		textAreaPrivateKey.setFocusable(false);
		textAreaPrivateKey.setBorder(border);
		enableDragAndDropPrivateKey();

		// Error label
		JLabel lblError = new JLabel("Error occured");
		lblError.setForeground(Color.RED);
		lblError.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblError.setHorizontalAlignment(SwingConstants.CENTER);
		lblError.setBounds(298, 413, 192, 27);
		lblError.setVisible(false);
		getContentPane().add(lblError);

		// Decrypt button
		JButton btnDecrypt = new JButton("Decrypt");
		btnDecrypt.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnDecrypt.setBounds(298, 336, 192, 56);
		btnDecrypt.addActionListener(e -> {
			if (encryptedMessage == null || privateKey == null) {
				if (encryptedMessage == null) {
					textAreaEncrypted.setBorder(BorderFactory.createLineBorder(Color.RED, 2, true));
				}

				if (privateKey == null) {
					textAreaPrivateKey.setBorder(BorderFactory.createLineBorder(Color.RED, 2, true));
				}
			} else {
				try {
					decryptedMessage = CryptographicAlgorithm.handleDecrypt(encryptedMessage, privateKey);
					lblError.setVisible(false);
					decryptedScene();
				} catch (Exception e1) {
					System.out.println("Error occured");
					lblError.setVisible(true);
					e1.printStackTrace();
				}
			}
		});
		getContentPane().add(btnDecrypt);

		// Back button
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnBack.setBounds(593, 336, 112, 56);
		btnBack.addActionListener(e -> {
			publicKey = null;
			privateKey = null;
			encryptedMessage = null;
			decryptedMessage = null;
			message = null;
			menuScene();
		});
		getContentPane().add(btnBack);

	}

	public void decryptedScene() {
		getContentPane().removeAll();
		getContentPane().repaint();
		// Header panel
		JPanel panel = new JPanel();
		panel.setBounds(-11, 0, 805, 66);
		panel.setBackground(new Color(3, 169, 244));
		getContentPane().add(panel);

		// Header label
		JLabel lblCryptoUtility = new JLabel("Crypto Utility");
		panel.add(lblCryptoUtility);
		lblCryptoUtility.setFont(new Font("Roboto Light", Font.PLAIN, 40));
		lblCryptoUtility.setHorizontalAlignment(SwingConstants.CENTER);

		// Page title
		JLabel lblResult = new JLabel("Result");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setFont(new Font("Roboto Thin", Font.PLAIN, 30));
		lblResult.setBounds(280, 86, 222, 40);
		getContentPane().add(lblResult);
		enableDragAndDropPrivateKey();

		// Back button
		JButton btnBack = new JButton("Menu");
		btnBack.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnBack.setBounds(593, 336, 112, 56);
		btnBack.addActionListener(e -> {
			publicKey = null;
			privateKey = null;
			encryptedMessage = null;
			decryptedMessage = null;
			message = null;
			menuScene();
		});

		// Decrypted message
		JTextArea txtMessage = new JTextArea(decryptedMessage);
		txtMessage.setFont(new Font("Roboto", Font.PLAIN, 25));
		txtMessage.setBounds(48, 139, 682, 175);
		txtMessage.setLineWrap(true);
		txtMessage.setWrapStyleWord(true);
		txtMessage.setEditable(false);
		getContentPane().add(txtMessage);
		getContentPane().add(btnBack);
	}

	public void keyGenScene() {
		getContentPane().removeAll();
		getContentPane().repaint();
		// Header panel
		JPanel panel = new JPanel();
		panel.setBounds(-11, 0, 805, 66);
		panel.setBackground(new Color(3, 169, 244));
		getContentPane().add(panel);

		// Header label
		JLabel lblCryptoUtility = new JLabel("Crypto Utility");
		panel.add(lblCryptoUtility);
		lblCryptoUtility.setFont(new Font("Roboto Light", Font.PLAIN, 40));
		lblCryptoUtility.setHorizontalAlignment(SwingConstants.CENTER);

		// Page title
		JLabel lblDecryption = new JLabel("Generate Keys");
		lblDecryption.setHorizontalAlignment(SwingConstants.CENTER);
		lblDecryption.setFont(new Font("Roboto Thin", Font.PLAIN, 30));
		lblDecryption.setBounds(280, 86, 222, 40);
		getContentPane().add(lblDecryption);

		// Back button
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnBack.setBounds(593, 336, 112, 56);
		btnBack.addActionListener(e -> {
			publicKey = null;
			privateKey = null;
			encryptedMessage = null;
			decryptedMessage = null;
			message = null;
			menuScene();
		});
		getContentPane().add(btnBack);

		// Seed fields
		JTextField txtSeed = new JTextField();
		txtSeed.setFont(new Font("Roboto", Font.PLAIN, 17));
		txtSeed.setToolTipText("Enter a seed");
		txtSeed.setBounds(160, 208, 158, 40);
		txtSeed.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
		txtSeed.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
			}

			@Override
			public void focusGained(FocusEvent e) {
				txtSeed.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
			}
		});
		getContentPane().add(txtSeed);
		txtSeed.setColumns(10);

		JLabel lblSeed = new JLabel("Enter a seed");
		lblSeed.setFont(new Font("Roboto", Font.PLAIN, 20));
		lblSeed.setHorizontalAlignment(SwingConstants.CENTER);
		lblSeed.setBounds(160, 169, 158, 33);
		getContentPane().add(lblSeed);

		// Length fields
		JTextField txtLength = new JTextField();
		txtLength.setFont(new Font("Roboto", Font.PLAIN, 17));
		txtLength.setToolTipText("Enter a seed");
		txtLength.setColumns(10);
		txtLength.setBounds(464, 208, 158, 40);
		txtLength.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
		txtLength.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
			}

			@Override
			public void focusGained(FocusEvent e) {
				txtLength.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
			}
		});
		getContentPane().add(txtLength);

		JLabel lblLength = new JLabel("Specify max length");
		lblLength.setHorizontalAlignment(SwingConstants.CENTER);
		lblLength.setFont(new Font("Roboto", Font.PLAIN, 20));
		lblLength.setBounds(440, 169, 207, 33);
		getContentPane().add(lblLength);

		// Error label
		JLabel lblError = new JLabel("Error occured");
		lblError.setForeground(Color.RED);
		lblError.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblError.setHorizontalAlignment(SwingConstants.CENTER);
		lblError.setBounds(298, 413, 192, 27);
		lblError.setVisible(false);
		getContentPane().add(lblError);

		// Generate keys button
		JButton btnGenerateKeys = new JButton("Generate  Keys");
		btnGenerateKeys.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnGenerateKeys.setBounds(300, 336, 181, 56);
		btnGenerateKeys.addActionListener(e -> {
			lblError.setVisible(false);
			if (txtLength.getText().equals("") || txtSeed.getText().equals("")) {
				if (txtLength.getText().equals("")) {
					txtLength.setBorder(BorderFactory.createLineBorder(Color.RED, 1, true));
				}
				if (txtSeed.getText().equals("")) {
					txtSeed.setBorder(BorderFactory.createLineBorder(Color.RED, 1, true));
				}
			} else {
				try {
					int seed = Integer.parseInt(txtLength.getText());
					CryptographicAlgorithm.handleKeyGen(txtLength.getText(), seed);
					lblError.setVisible(false);
					getContentPane().repaint();
					keysGeneratedScene();
				} catch (NumberFormatException e1) {
					txtLength.setBorder(BorderFactory.createLineBorder(Color.RED, 1, true));
				} catch (Exception e1) {
					lblError.setVisible(true);
					e1.printStackTrace();
				}
			}
		});

		getContentPane().add(btnGenerateKeys);

	}

	public void keysGeneratedScene() {
		getContentPane().removeAll();
		getContentPane().repaint();

		// Header panel
		JPanel panel = new JPanel();
		panel.setBounds(-11, 0, 805, 66);
		panel.setBackground(new Color(3, 169, 244));
		getContentPane().add(panel);

		// Header label
		JLabel lblCryptoUtility = new JLabel("Crypto Utility");
		panel.add(lblCryptoUtility);
		lblCryptoUtility.setFont(new Font("Roboto Light", Font.PLAIN, 40));
		lblCryptoUtility.setHorizontalAlignment(SwingConstants.CENTER);

		// Page title
		JLabel lblDecryption = new JLabel("Keys Succesfully Generated");
		lblDecryption.setHorizontalAlignment(SwingConstants.CENTER);
		lblDecryption.setFont(new Font("Roboto Thin", Font.PLAIN, 30));
		lblDecryption.setBounds(197, 86, 387, 40);
		getContentPane().add(lblDecryption);

		// Back button
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Roboto", Font.PLAIN, 18));
		btnBack.setBounds(593, 336, 112, 56);
		btnBack.addActionListener(e -> {
			publicKey = null;
			privateKey = null;
			encryptedMessage = null;
			decryptedMessage = null;
			message = null;
			menuScene();
		});
		getContentPane().add(btnBack);

		JLabel lblKeys = new JLabel("Keys saved at \"keys\" folder");
		lblKeys.setHorizontalAlignment(SwingConstants.CENTER);
		lblKeys.setFont(new Font("Roboto", Font.PLAIN, 30));
		lblKeys.setBounds(197, 223, 387, 40);
		getContentPane().add(lblKeys);
	}

	private void enableDragAndDropPublicKey() {
		new DropTarget(textAreaPublicKey, new DropTargetListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent e) {
				try {
					// Accept the drop first, important!
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

					// Get the files that are dropped as List
					List<File> list = (List<File>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					File file = (File) list.get(0);
					textAreaPublicKey.read(new FileReader(file), null);
					publicKey = textAreaPublicKey.getText();
					textAreaPublicKey.setText("Public keys accepted");
					textAreaPublicKey.setBackground(Color.WHITE);
					textAreaPublicKey.setBorder(BorderFactory.createLineBorder(new Color(3, 169, 244), 2, true));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			public void dropActionChanged(DropTargetDragEvent dtde) {
			}

			public void dragOver(DropTargetDragEvent dtde) {
			}

			public void dragExit(DropTargetEvent dte) {
				textAreaPublicKey.setBackground(Color.WHITE);
			}

			public void dragEnter(DropTargetDragEvent dtde) {
				textAreaPublicKey.setBackground(new Color(230, 230, 230));
			}
		});
	}

	private void enableDragAndDropPrivateKey() {
		new DropTarget(textAreaPrivateKey, new DropTargetListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent e) {
				try {
					// Accept the drop first, important!
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

					// Get the files that are dropped as List
					List<File> list = (List<File>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					File file = (File) list.get(0);
					textAreaPrivateKey.read(new FileReader(file), null);
					privateKey = textAreaPrivateKey.getText();
					textAreaPrivateKey.setText("Private keys accepted");
					textAreaPrivateKey.setBackground(Color.WHITE);
					textAreaPrivateKey.setBorder(BorderFactory.createLineBorder(new Color(3, 169, 244), 2, true));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			public void dropActionChanged(DropTargetDragEvent dtde) {
			}

			public void dragOver(DropTargetDragEvent dtde) {
			}

			public void dragExit(DropTargetEvent dte) {
				textAreaPrivateKey.setBackground(Color.WHITE);
			}

			public void dragEnter(DropTargetDragEvent dtde) {
				textAreaPrivateKey.setBackground(new Color(230, 230, 230));
			}
		});
	}

	private void enableDragAndDropEncrypted() {
		new DropTarget(textAreaEncrypted, new DropTargetListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent e) {
				try {
					// Accept the drop first, important!
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

					// Get the files that are dropped as List
					List<File> list = (List<File>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					File file = (File) list.get(0);
					textAreaEncrypted.read(new FileReader(file), null);
					encryptedMessage = textAreaEncrypted.getText();
					textAreaEncrypted.setText("Message accepted");
					textAreaEncrypted.setBackground(Color.WHITE);
					textAreaEncrypted.setBorder(BorderFactory.createLineBorder(new Color(3, 169, 244), 2, true));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			public void dropActionChanged(DropTargetDragEvent dtde) {
			}

			public void dragOver(DropTargetDragEvent dtde) {
			}

			public void dragExit(DropTargetEvent dte) {
				textAreaEncrypted.setBackground(Color.WHITE);
			}

			public void dragEnter(DropTargetDragEvent dtde) {
				textAreaEncrypted.setBackground(new Color(230, 230, 230));
			}
		});
	}
}
