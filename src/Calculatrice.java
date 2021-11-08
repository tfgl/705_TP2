package calculator.generic;

// pour l'interface graphique
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClient;

import java.net.URL;

import javax.swing.*;

/**
 * La classe <code>Calculatrice</code> est l'interface graphique d'une
 * calculatrice simple. Le calcul des rsultats sera effectu par un moteur de
 * calcul spar spcifi par l'interface <code>IMoteurCalcul</code>.
 * 
 */

// fenetre qui gere elle meme ses evenements
public class Calculatrice extends JFrame implements ActionListener {
	// tableau de boutons
	private JButton[] buttons;

	// ecran de la calculatrice
	private TextField ecran;

	// tableau des legendes des boutons
	private static String[] labels = { "(", ")", "EFF", "OFF", "7", "8", "9",
			"*", "4", "5", "6", "/", "1", "2", "3", "-", "0", ".", "=", "+" };

	// pour savoir s'il faut effacer l'ecran ou non
	boolean estCalcule = false;

    public XmlRpcClient client;

	// constructeur
	public Calculatrice() {
		// la fenetre a pour titre "Calculatrice"
		super("Calculatrice");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://localhost:54000"));
            client = new XmlRpcClient();
            client.setConfig(config);
        } catch (Exception e) {
        }

		setLayout(new BorderLayout());
		// l'ecran occupe le haut de la fenetre
		ecran = new TextField("");
		add(BorderLayout.NORTH, ecran);

		// creation du clavier
		JPanel clavier = new JPanel();
		clavier.setLayout(new GridLayout(5, 4));

		// on cree chaque bouton
		buttons = new JButton[labels.length];
		for (int i = 0; i < labels.length; i++) {
			buttons[i] = new JButton(labels[i]);
			clavier.add(buttons[i]);
			buttons[i].addActionListener(this);
		}

		// ajout du clavier au centre de la fenetre
		add(BorderLayout.CENTER, clavier);
		pack();
		setVisible(true);
	}

	// gestion des clics sur bouton
	public void actionPerformed(ActionEvent e) {
		// recuperation de la commande associee
		// (i.e. la legende du bouton)
		String s = e.getActionCommand();
		// chaque commande est identifiable par son premier caractere
		char c = s.charAt(0);
		switch (c) {
		// effacement de l'ecran
		case 'E':
			ecran.setText("");
			break;
		// on quitte
		case 'O':
			System.exit(0);
		// calcul et affichage de l'expression
		case '=':
			try {
                Object[] params = new Object[]{ecran.getText()};

                //Appel de la méthode à distance "calculer"
                Double resultat = (Double) client.execute("MoteurCalculSimple.calculer", params);
                //*************************************************************//

                // affichage du resultat
                ecran.setText(resultat.toString());
                estCalcule = true;
			}
			// plantage du programme
			catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"<html>Erreur interne :<br>" + ex.getMessage()
								+ "<html>");
				ecran.selectAll();
			}
			break;
		// toutes les autres touches : ajout du caractere correspondant
		default:
			if (estCalcule)
				ecran.setText("");
			StringBuffer temp = new StringBuffer(ecran.getText());
			temp.append(c);
			ecran.setText(new String(temp));
			estCalcule = false;
			break;
		}
	}

    public static void main(String[] arg) {
        Calculatrice calc = new Calculatrice();
    }
}
