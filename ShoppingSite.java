import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.DecimalFormat;
import javax.swing.text.MaskFormatter;

public class ShoppingSite implements ActionListener{
	
	/**
	 * The main method will call the method to create the user interface for
	 * the main shopping site.
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			ShoppingSite site = new ShoppingSite();
			public void run() {
				site.create();
			}
		});
	}
	
	/**
	 * The actionPerformed method controls the actions the will be performed
	 * when each button is pressed.
	 */
	public void actionPerformed(ActionEvent e) {
		//Button action that allows the admin to add products to the database.
		if(e.getSource() == productAddButton) {
			addButtonDialog.setVisible(true);
			addButtonDialog.setLocationRelativeTo(shoppingSite);
			shoppingSite.setEnabled(false);
		}
		
		//Button action that allows the admin to edit a product that was selected.
		if(e.getSource() == productEditButton) {
			int row = productTable.getSelectedRow();
			//if no product is selected then an error message is displayed.
			if(row == -1) {
				JOptionPane.showMessageDialog(shoppingSite,
						"No rows have been selected!",
						"Error", 
						JOptionPane.OK_OPTION
				);
			//if at least one row is selected then the selected product that is closes to the
			//top of the list is selected for editting.
			} else {
				addButton.setText("Edit");
				addButtonDialog.setTitle("Edit Product");
				String product = (String)productTableModel.getValueAt(row, 0);
				String price = (String)productTableModel.getValueAt(row, 1);
				setAddButtonDialogFrame(product,price,true);
				addButtonDialog.setLocationRelativeTo(shoppingSite);
				shoppingSite.setEnabled(false);
			}
		}
		
		//Button action to remove all products that have been selected from the product database
		if(e.getSource() == productRemoveButton) {
			int numOfRows = productTable.getSelectedRows().length;
			//Message is displayed if no items have be selected
			if(numOfRows == 0) {
				JOptionPane.showMessageDialog(shoppingSite,
						"No rows have been selected!",
						"Error", 
						JOptionPane.OK_OPTION
				);
			} else {
				//Displays a message to make sure the admin wants to remove the selected rows
				int choice = JOptionPane.showConfirmDialog(shoppingSite, 
						"Are you sure you want to remove selected products.", 
						"Confirm Removal", JOptionPane.YES_NO_OPTION);
				//If the user selects yes then the selected rows are removed
				if(choice == 0) {
					for(int i=0; i<numOfRows; i++) {
						int index = productTable.getSelectedRow();
						productTableModel.removeRow(index);
						shoppingTableModel.removeRow(index);
					}
				}
				productTable.clearSelection();
			}
		}
		
		//Button action that add the new product or edit the a current product to the database.
		if(e.getSource() == addButton) {
			String price = priceField.getText();
			//There must a price specified otherwise an error message is displayed
			if(price.equals("")) {
				JOptionPane.showMessageDialog(addButtonDialog,
						"No price was specified.", "Error",
						JOptionPane.ERROR_MESSAGE);
			//If the price string can't be parsed as a double then an error message is displayed.
			} else if(!isDouble(price)) {
				JOptionPane.showMessageDialog(addButtonDialog,
						"The price must be a non-negative number.", "Error",
						JOptionPane.ERROR_MESSAGE);
			//If the price is negative then an error message is displayed.
			} else if(Double.parseDouble(price) < 0){
				JOptionPane.showMessageDialog(addButtonDialog,
						"The price must be a non-negative number.", "Error",
						JOptionPane.ERROR_MESSAGE);
			//The product is now added or edited in the database.
			} else {
				Object[] rowValues = {productNameField.getText(),
						priceField.getText()};
				//If the button text is set to 'Add' then the product must be added to the database.
				if(addButton.getText().equals("Add")){
					productTableModel.addRow(rowValues);
					shoppingTableModel.addRow(rowValues);
				//Here the button text must be 'Edit' so the product will be edited in the database.
				} else {
					int row = productTable.getSelectedRow();
					productTableModel.setValueAt(rowValues[0], row, 0);
					productTableModel.setValueAt(rowValues[1], row, 1);
					shoppingTableModel.setValueAt(rowValues[0], row, 0);
					shoppingTableModel.setValueAt(rowValues[1], row, 1);
				}
			}
			//Makes sure the button text returns to 'Add' after editing occurs.
			if(addButton.getText().equals("Edit")) {
				addButton.setText("Add");
				addButtonDialog.setTitle("Add Product");
			}
			setAddButtonDialogFrame("","",false);
			shoppingSite.setEnabled(true);
		}
		
		//Button action that closes the dialog to add or edit a product with out changing the database.
		if(e.getSource() == cancelButton) {
			setAddButtonDialogFrame("","",false);
			shoppingSite.setEnabled(true);
		}
		
		//Button action that opens the dialog so the user can review the products they want to buy and
		//allows them to enter their credit card and shipping address information.
		if(e.getSource() == confirmPurchaseButton) {
			//Resets the table so new products the user wants to buy can be placed in it.
			for(int row=purchaseTableModel.getRowCount(); row > 0; row--) {
				purchaseTableModel.removeRow(row-1);
			}
			
			double total = 0; //Tracks the total cost of the users purchase
			//Finds the total cost of the users purchase and adds the products the user wants to purchase
			//to the purchaseTableModel.
			for(int row=0; row<shoppingTableModel.getRowCount(); row++) {
				JSpinner qty = (JSpinner)shoppingTableModel.getValueAt(row, 2);
				String price = (String)shoppingTableModel.getValueAt(row, 1);
				if((Integer)qty.getValue() != 0){
					Object[] rowData = {shoppingTableModel.getValueAt(row, 0),
							            price,
							           (Integer)qty.getValue()};
					total += (Integer)qty.getValue()*Double.parseDouble(price);
					purchaseTableModel.addRow(rowData);
				}
			}
			//Displays the total cost of the purchase.
			totalTextField.setText(currencyFormatter.format(total));
			
			//A message is displayed if the user hits the purchase button without selecting any products
			//to buy.
			if(purchaseTableModel.getRowCount() == 0) {
				JOptionPane.showMessageDialog(shoppingSite,
						"You haven't selected any items.", "Error",
						JOptionPane.OK_OPTION);
			} else {
				confirmPurchaseDialog.setVisible(true);
				confirmPurchaseDialog.setLocationRelativeTo(shoppingSite);
				shoppingSite.setEnabled(false);
			}
		}
		
		//Button action that will cancel the purchase a user has made before it is completed.
		if(e.getSource() == cancelPurchaseButton) {
			confirmPurchaseDialog.setVisible(false);
			shoppingSite.setEnabled(true);
		}
		
		//Button action that will complete the users purchase.
		if(e.getSource() == completePurchaseButton) {
			//If any of the information fields are empty then the purchase can't be completed so
			//and error message is displayed.
			if(cardHolderTextField.getText().equals("") ||
					addressTextField.getText().equals("") ||
					cityTextField.getText().equals("") ||
					stateTextField.getText().equals("")
					){
				JOptionPane.showMessageDialog(confirmPurchaseDialog,
						"One or more information fields are blank.", "Error",
						JOptionPane.ERROR_MESSAGE);
			//Checks if zipcode has exactly 5 digits.
			} else if(zipTextField.getText().equals("     ")) {
				JOptionPane.showMessageDialog(confirmPurchaseDialog,
						"Invalid zipcode. Must have exactly 5 digits.", "Error",
						JOptionPane.ERROR_MESSAGE);
			//Checks if card number has exactl 16 digits.
			} else if(cardNumberTextField.getText().equals("                ")) {
				JOptionPane.showMessageDialog(confirmPurchaseDialog,
						"Invalid credit card number. Must have 16 digits.", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				confirmPurchaseDialog.setVisible(false);
				completedPurchaseDialog = createCompletedPurchaseDialog();
				completedPurchaseDialog.setVisible(true);
				completedPurchaseDialog.setLocationRelativeTo(shoppingSite);
			}
		}
		
		//The continue button does the same thing as part of the finish button action 
		//on the completedPurchaseDialog.  This resets the quantity field for each product in
		//the shopping table back to 0 and adds the purchaseScrollPane back into the confirmPurchaseDialog
		//since it was moved to the completedPurchaseDialog.
		if(e.getSource() == continueButton || e.getSource() == finishButton) {
			completedPurchaseDialog.setVisible(false);
			for(int row = 0; row < shoppingTableModel.getRowCount(); row++) {
				JSpinner qty = (JSpinner)shoppingTableModel.getValueAt(row, 2);
				qty.setValue((Integer)0);
			}
			shoppingTable.repaint();
			shoppingSite.setEnabled(true);
			
			GridBagConstraints gc = new GridBagConstraints();
			gc.weightx=1;gc.weighty=1;
			gc.gridy = 1;
			gc.fill = GridBagConstraints.HORIZONTAL;
			confirmPurchaseDialog.add(purchaseScrollPane,gc);
		}
		
		//If the finish button was used then each field in the information is reset.
		if(e.getSource() == finishButton) {
			cardHolderTextField.setText("");
			cardNumberTextField.setText("");
			addressTextField.setText("");
			cityTextField.setText("");
			stateTextField.setText("");
			zipTextField.setText("");
			
			tabbedPane.setSelectedIndex(0);
		}
	}
	
	/**
	 * Used to control the dialog that allows the user to add or edit products in the
	 * collection of products.
	 * 
	 * Set the name and price of the product and whether the dialog will be shown or not.
	 * 
	 * @param product
	 * 		the name of the product
	 * @param price
	 * 		the price of the product
	 * @param show
	 * 		boolean to set whether dialog is shown
	 */
	private void setAddButtonDialogFrame(String product, String price, boolean show) {
		productNameField.setText(product);
		priceField.setText(price);
		addButtonDialog.setVisible(show);
	}
	
	/**
	 * Checks whether a string can be parsed as a double
	 * @param str
	 * 		the string that is check to see if it can be parsed as a double
	 * @return
	 * 		true if the string is a double number
	 */
	private boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
		} catch(NumberFormatException ex) {
			return false;
		}
		if(str.indexOf('d') != -1){
			return false;
		}
		return true;
	}
	
	/**
	 * Creates a mask formatter based on the input string that is used to allow on certain
	 * text strings in some text fields.
	 * 
	 * @param str
	 * 		the string the contains the format used in the mask formatter.
	 * @return
	 *  	the mask formatter based on the given string.
	 */
	private MaskFormatter createFormat(String str) {
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(str);
		} catch(java.text.ParseException ex) {
			System.err.println("formatter is bad: " + ex.getMessage());
	        System.exit(-1);
		}
		return formatter;
	}
	
	//Formatter to convert a double string into a currency format
	private DecimalFormat currencyFormatter = new DecimalFormat("$0.00");
	
	/**
	 * The rest of these variables are used to build the user interface for the
	 * shopping site.
	 */
	private JFrame shoppingSite;
	private JTabbedPane tabbedPane;
	
	private JPanel productPanel;
	private JTable productTable;
	private DefaultTableModel productTableModel;
	private JScrollPane productScrollPane;
	private JButton productAddButton, productEditButton, productRemoveButton;
	
	private JPanel shoppingPanel;
	private JTable shoppingTable;
	private ShoppingTableModel shoppingTableModel;
	private JScrollPane shoppingScrollPane;
	private JButton confirmPurchaseButton;
	
	private JDialog addButtonDialog;
	private JTextField productNameField, priceField;
	private JButton addButton, cancelButton;
	
	private JDialog confirmPurchaseDialog;
	private JTable purchaseTable;
	private DefaultTableModel purchaseTableModel;
	private JScrollPane purchaseScrollPane;
	private JTextField totalTextField;
	private JTextField cardHolderTextField;
	private JFormattedTextField cardNumberTextField;
	private JComboBox<String> exMonthComboBox, exYearComboBox;
	private JTextField addressTextField, cityTextField, stateTextField;
	private JFormattedTextField zipTextField;
	private JButton completePurchaseButton, cancelPurchaseButton;
	
	private JDialog completedPurchaseDialog;
	private JButton continueButton, finishButton;
	
	/**
	 * Create the dialog that allows the user to review the items the selected to purchase
	 * and to enter the credit card and address information so the products can be shipped.
	 * 
	 * @return
	 * 		returns the created dialog.
	 */
	private JDialog createConfirmPurchaseDialog() {
		GridBagConstraints gc = new GridBagConstraints();
		JDialog dialog = new JDialog(shoppingSite, "Confirm Purchase");
		dialog.setLayout(new GridBagLayout());
		
		gc.weightx=1;gc.weighty=1;
		gc.anchor = GridBagConstraints.WEST;
		dialog.add(new JLabel("<html><font size=4>Items:</font></html>"),gc);
		
		gc.gridy = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		
		purchaseTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 5910009624924957185L;
			//Prevents the purchaseTable from allowing the cells to be editable by a user.
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		purchaseTableModel.addColumn("Product");
		purchaseTableModel.addColumn("Price");
		purchaseTableModel.addColumn("Qty");
		
		purchaseTable = new JTable(purchaseTableModel);
		purchaseTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		purchaseTable.getColumnModel().getColumn(1).setPreferredWidth(55);
		purchaseTable.getColumnModel().getColumn(2).setPreferredWidth(25);
		purchaseTable.getTableHeader().setReorderingAllowed(false);
		purchaseTable.getTableHeader().setResizingAllowed(false);
		purchaseTable.getColumnModel().getColumn(1).setCellRenderer(new PriceCellRenderer());
		
		purchaseScrollPane = new JScrollPane(purchaseTable);
		purchaseScrollPane.setPreferredSize(new Dimension(300,175));
		dialog.add(purchaseScrollPane, gc);
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Total:"));
		totalTextField = new JTextField();
		totalTextField.setPreferredSize(new Dimension(80,18));
		totalTextField.setEditable(false);
		panel.add(totalTextField);
		
		gc.gridy = 2;
		dialog.add(panel, gc);
		
		gc.gridy = 3;
		dialog.add(new JSeparator(), gc);
		
		gc.gridy = 4;
		dialog.add(new JLabel("<html><font size=4>Credit Card Information</font></html>"),gc);
		
		gc.gridy = 5;
		dialog.add(createCardInfoPanel(),gc);
		
		gc.gridy = 6;
		dialog.add(new JSeparator(), gc);
		
		gc.gridy = 7;
		dialog.add(new JLabel("<html><font size=4>Shipping Information</font></html>"),gc);
		
		gc.gridy = 8;
		dialog.add(createShippingInformation(),gc);
		
		gc.gridy = 9;
		dialog.add(new JSeparator(),gc);
		
		gc.gridy = 10; gc.weightx = 0;
		Box buttonBox = Box.createHorizontalBox();
		completePurchaseButton = new JButton("Complete Purchase");
		buttonBox.add(completePurchaseButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		cancelPurchaseButton = new JButton("Cancel");
		buttonBox.add(cancelPurchaseButton);
		
		dialog.add(buttonBox, gc);
		
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				shoppingSite.setEnabled(true);
				confirmPurchaseDialog.setVisible(false);
			}
		});
		dialog.pack();
		return dialog;
	}
	
	/**
	 * Creates a JPanel that displays an area for the user to enter their address.
	 * Used with the confirmPurchaseDialog.
	 * 
	 * @return
	 * 		returns the created panel.
	 */
	private JPanel createShippingInformation() {
		GridBagConstraints gc = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());
		gc.weightx = 1; gc.weighty = 1;
		
		gc.anchor = GridBagConstraints.EAST;
		panel.add(new JLabel("Address: "),gc);
		
		gc.gridx = 1; gc.gridwidth = 4;
		gc.fill = GridBagConstraints.HORIZONTAL;
		addressTextField = new JTextField();
		addressTextField.setPreferredSize(new Dimension(300,20));
		panel.add(addressTextField, gc);
		
		gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 1;
		gc.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("City: "),gc);
		
		gc.gridx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		cityTextField = new JTextField();
		cityTextField.setPreferredSize(new Dimension(150,20));
		panel.add(cityTextField,gc);
		
		gc.gridx = 2;
		gc.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("State: "),gc);
		
		gc.gridx = 3;
		gc.fill = GridBagConstraints.HORIZONTAL;
		stateTextField = new JTextField();
		stateTextField.setPreferredSize(new Dimension(50,20));
		panel.add(stateTextField, gc);
		
		gc.gridx = 0; gc.gridy = 2;
		gc.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Zipcode: "),gc);
		
		gc.gridx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		zipTextField = new JFormattedTextField(createFormat("#####"));
		panel.add(zipTextField, gc);
		
		gc.gridx = 2;
		panel.add(new JLabel("(Must be 5 digits.)"),gc);
		
		return panel;
	}
	
	/**
	 * Creates a JPanel that displays an area for the user to enter their credit card information
	 * Used with the confirmPurchaseDialog.
	 * 
	 * @return
	 * 		returns the created panel.
	 */
	private JPanel createCardInfoPanel() {
		GridBagConstraints gc = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());
		
		gc.weightx = 1; gc.weighty = 1;
		gc.anchor = GridBagConstraints.EAST;
		panel.add(new JLabel("Cardholder Name: "),gc);
		
		gc.gridx = 1; gc.gridwidth = 2;
		gc.fill = GridBagConstraints.HORIZONTAL;
		cardHolderTextField = new JTextField();
		cardHolderTextField.setPreferredSize(new Dimension(300,20));
		panel.add(cardHolderTextField,gc);
		
		gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 1;
		gc.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Card Number: "),gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		cardNumberTextField = new JFormattedTextField(createFormat("################"));
		cardNumberTextField.setPreferredSize(new Dimension(120,20));
		panel.add(cardNumberTextField, gc);
		
		gc.gridx = 2;
		gc.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("(Must be 16 digits.)"),gc);
		
		gc.gridx = 0; gc.gridy = 2;
		gc.anchor = GridBagConstraints.EAST;
		panel.add(new JLabel("Expiration Date: "),gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		Box comboBoxBox = Box.createHorizontalBox();
		String[] months = new String[12];
		for(int i=0; i<12; i++) {
			if(i < 9) {
				months[i] = "0" + Integer.toString(i+1);
			} else {
				months[i] = Integer.toString(i+1);
			}
		}
		exMonthComboBox = new JComboBox<String>(months);
		comboBoxBox.add(exMonthComboBox,gc);
		
		gc.gridx = 1;
		String[] years = new String[20];
		for(int i = 0; i<20; i++) {
			years[i] = Integer.toString(2011 + i);
		}
		exYearComboBox = new JComboBox<String>(years);
		comboBoxBox.add(exYearComboBox,gc);
		
		panel.add(comboBoxBox,gc);
		
		return panel;
	}
	
	/**
	 * Creates the dialog to allow an admin to either add products to the database of products or
	 * edit the products currently in the database.
	 * 
	 * @return
	 *  	returns the created panel.
	 */
	private JDialog createAddButtonDialog() {
		GridBagConstraints gc = new GridBagConstraints();
		JDialog dialog = new JDialog(shoppingSite, "Add Product");
		dialog.setLayout(new GridBagLayout());
		
		gc.gridx = 0;gc.gridy = 0;
		gc.gridwidth = 1; gc.gridheight = 1;
		gc.anchor = GridBagConstraints.EAST;
		dialog.add(new JLabel("Product Name:   "),gc);
		
		gc.gridx = 0; gc.gridy = 1;
		dialog.add(new JLabel("Price: $"),gc);
		
		gc.gridx = 1; gc.gridy = 0;
		productNameField = new JTextField();
		productNameField.setPreferredSize(new Dimension(300,20));
		dialog.add(productNameField,gc);
		
		gc.gridy = 1;
		priceField = new JTextField();
		priceField.setPreferredSize(new Dimension(300,20));
		dialog.add(priceField,gc);
		
		gc.gridx = 1; gc.gridy = 2;
		gc.anchor = GridBagConstraints.WEST;
		addButton = new JButton("Add");
		addButton.setPreferredSize(new Dimension(85,25));
		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(85,25));
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(addButton);
		buttonBox.add(Box.createHorizontalStrut(20));
		buttonBox.add(cancelButton);
		dialog.add(buttonBox, gc);
		
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				shoppingSite.setEnabled(true);
				addButtonDialog.setVisible(false);
			}
		});
		dialog.pack();
		return dialog;
	}
	
	/**
	 * Creates the JPanel that creates a user interface that allows an admin to see the items in
	 * the product database and creates buttons that allow the admin to add, edit, or remove products
	 * 
	 * @return
	 *  	returns the created panel.
	 */
	private JPanel createProductPanel() {
		GridBagConstraints gc = new GridBagConstraints();
		productPanel = new JPanel();
		productPanel.setLayout(new GridBagLayout());
		
		gc.gridx = 0; gc.gridy = 0;
		gc.gridwidth = 3;
		gc.weighty = .1;
		gc.anchor = GridBagConstraints.NORTH;
		
		productTableModel = new DefaultTableModel(){
			private static final long serialVersionUID = -6219378442663413762L;
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		productTableModel.addColumn("Product");
		productTableModel.addColumn("Price");
		
		productTable = new JTable(productTableModel);
		productTable.getColumnModel().getColumn(0).setPreferredWidth(235);
		productTable.getTableHeader().setReorderingAllowed(false);
		productTable.getTableHeader().setResizingAllowed(false);
		productTable.getColumnModel().getColumn(1).setCellRenderer(new PriceCellRenderer());
		productScrollPane = new JScrollPane(productTable);
		productScrollPane.setPreferredSize(new Dimension(300,300));
		productPanel.add(productScrollPane,gc);
		
		productAddButton = new JButton("Add");
		productAddButton.setPreferredSize(new Dimension(85,25));
		gc.gridy = 1;
		gc.gridwidth = 1;
		gc.weightx = 1; gc.weighty = 1;
		productPanel.add(productAddButton, gc);
		
		productEditButton = new JButton("Edit");
		productEditButton.setPreferredSize(new Dimension(85,25));
		gc.gridx = 1;
		productPanel.add(productEditButton,gc);
		
		productRemoveButton = new JButton("Remove");
		productRemoveButton.setPreferredSize(new Dimension(85,25));
		gc.gridx = 2;
		productPanel.add(productRemoveButton, gc);
		
		return productPanel;
	}
	
	/**
	 * Creates the JPanel that creates a user interface that allows a user to see all products in the database
	 * and allows them select the products they want by editting the amount of quantity for each product.
	 * 
	 * @return
	 * 		returns the created panel.
	 */
	private JPanel createShoppingPanel() {
		GridBagConstraints gc = new GridBagConstraints();
		shoppingPanel = new JPanel();
		shoppingPanel.setLayout(new GridBagLayout());
		
		gc.gridx = 0; gc.gridy = 0;
		gc.weighty = .1;
		gc.anchor = GridBagConstraints.NORTH;
		
		shoppingTableModel = new ShoppingTableModel();
		
		shoppingTable = new JTable(shoppingTableModel);
		shoppingTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		shoppingTable.getColumnModel().getColumn(1).setPreferredWidth(65);
		shoppingTable.getColumnModel().getColumn(1).setCellRenderer(new PriceCellRenderer());
		shoppingTable.getColumnModel().getColumn(2).setPreferredWidth(32);
		shoppingTable.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditorRenderer());
		shoppingTable.getColumnModel().getColumn(2).setCellRenderer(new SpinnerEditorRenderer());
		shoppingTable.getTableHeader().setReorderingAllowed(false);
		shoppingTable.getTableHeader().setResizingAllowed(false);
		
		shoppingScrollPane = new JScrollPane(shoppingTable);
		shoppingScrollPane.setPreferredSize(new Dimension(300,300));
		shoppingPanel.add(shoppingScrollPane,gc);
		
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.CENTER;
		confirmPurchaseButton = new JButton("Purchase");
		confirmPurchaseButton.setPreferredSize(new Dimension(100,25));
		shoppingPanel.add(confirmPurchaseButton,gc);
		
		return shoppingPanel;
	}
	
	/**
	 * Creates a dialog that displays each item the user has purchased and the address that the items will be 
	 * shipped to.  This is the only 'create' method not used in the method create().  This dialog is created
	 * when the used clicks the appropriate button.
	 * 
	 * @return
	 * 		returns the created panel.
	 */
	private JDialog createCompletedPurchaseDialog() {
		GridBagConstraints gc = new GridBagConstraints();
		JDialog dialog= new JDialog(shoppingSite,"Completed Purchase");
		dialog.setLayout(new GridBagLayout());
		gc.weightx = 1; gc.weighty = 1;
		
		gc.anchor = GridBagConstraints.WEST;
		dialog.add(new JLabel("<html><font size=4>The Following Items Have Been Ordered:</font></html>"),gc);
		
		gc.gridy = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		dialog.add(purchaseScrollPane,gc);
		
		
		gc.gridy = 2;
		gc.fill = GridBagConstraints.HORIZONTAL;
		dialog.add(new JSeparator(),gc);
		
		gc.gridy = 3;
		gc.fill = GridBagConstraints.NONE;
		dialog.add(new JLabel("<html><font size=4>The Items Will Arrive At:</font></html>"),gc);
		
		String address = addressTextField.getText();
		String city = cityTextField.getText();
		String state = stateTextField.getText();
		String zip = zipTextField.getText();
		
		gc.gridy = 4;
		gc.anchor = GridBagConstraints.CENTER;
		dialog.add(new JLabel("<html><font size=5>"+address+"</font></html>"),gc);
		
		gc.gridy = 5;
		dialog.add(new JLabel("<html><font size=5>" +
				city + ", " + state + " " + zip + "</font></html>"),gc);
		
		gc.gridy = 6;
		gc.anchor = GridBagConstraints.WEST;
		dialog.add(new JLabel("<html><font size=4>They will arrive in 3-5 business days!</font></html>"),gc);
		
		gc.gridy = 7;
		Box buttonBox = Box.createHorizontalBox();
		continueButton = new JButton("Continue Shopping");
		//Needs to add the button to the action listener.
		continueButton.addActionListener(this);
		buttonBox.add(continueButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		finishButton = new JButton("Finish");
		//Needs to add the button to action listener.
		finishButton.addActionListener(this);
		buttonBox.add(finishButton);
		dialog.add(buttonBox,gc);
		
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				shoppingSite.setEnabled(true);
				completedPurchaseDialog.setVisible(false);
			}
		});
		dialog.pack();
		return dialog;
	}
	
	/**
	 * The method that will create every panel and dialog used for the user interface.
	 */
	private void create() {
		shoppingSite = new JFrame("Shopping Site");
		shoppingSite.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabbedPane = new JTabbedPane();
		
		//A tabbed pane is created so you can switch from the product page, which add products, to the shopping page,
		//to choose the products you want to buy.
		tabbedPane.addTab("Product Page", createProductPanel());
		tabbedPane.addTab("Shopping Page", createShoppingPanel());
		tabbedPane.setPreferredSize(new Dimension(325,350));
		shoppingSite.add(tabbedPane);
		
		shoppingSite.pack();
		shoppingSite.setLocationRelativeTo(null);
		shoppingSite.setResizable(false);
		shoppingSite.setVisible(true);
		
		addButtonDialog = createAddButtonDialog();
		addButtonDialog.setVisible(false);
		
		confirmPurchaseDialog = createConfirmPurchaseDialog();
		confirmPurchaseDialog.setVisible(false);
		
		//Adds each button to the action listener.
		productAddButton.addActionListener(this);
		productEditButton.addActionListener(this);
		productRemoveButton.addActionListener(this);
		addButton.addActionListener(this);
		cancelButton.addActionListener(this);
		confirmPurchaseButton.addActionListener(this);
		completePurchaseButton.addActionListener(this);
		cancelPurchaseButton.addActionListener(this);
	}
	
	/**
	 * This class extends the AbstractTableModel in order to be a model for the shopping table that allows a
	 * JSpinner to be used in one of the columns of the table.
	 * 
	 * The JSpinner is used so the user can select the quantity (from 0 to 99) of a product they want.
	 * 
	 */
	private class ShoppingTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 2010083273898973516L;

		private String[] columns = {"Product","Price","Qty"}; //Names of the columns in the table
		
		//Structure that holds the data for each row
		private ArrayList<ShoppingTableData> data = new ArrayList<ShoppingTableData>();
		
		public int getColumnCount() {
			return 3;
		}
		
		public int getRowCount() {
			return data.size();
		}
		
		public Object getValueAt(int row, int column) {
			ShoppingTableData rowdata = (ShoppingTableData)data.get(row);
			if(column == 0)
				return rowdata.PRODUCT;
			if(column == 1) 
				return rowdata.PRICE;
			if(column == 2)
				return rowdata.SPINNER;
			return null;
		}
		
		public void setValueAt(Object value, int row, int col) {
			ShoppingTableData rowdata = (ShoppingTableData)data.get(row);
			if(col == 0) {
				rowdata.PRODUCT = (String)value;
			} else if(col == 1) {
				rowdata.PRICE = (String)value;
			}
		}
		
		public String getColumnName(int col) {
			return columns[col];
		}
		
		public void addRow(Object[] rowdata) {
			String product = (String)rowdata[0];
			String price = (String)rowdata[1];
			data.add(new ShoppingTableData(product, price));
			
			//This forces the table to display the new row of data
			fireTableDataChanged();
		}
		
		public void removeRow(int row) {
			data.remove(row);
			
			fireTableDataChanged();
		}
		
		public boolean isCellEditable(int row, int column) {
			return column==2;
		}
	}
	
	/**
	 * A final class which encapsulates the data for one row of the table that uses the ShoppingTableModel.
	 */
	final private class ShoppingTableData {
		public String PRODUCT;
		public String PRICE;
		public JSpinner SPINNER;
		
		public ShoppingTableData(String prod, String price) {
			PRODUCT = prod;
			PRICE = price;
			SPINNER = new JSpinner(new SpinnerNumberModel(0,0,99,1));
		}
	}
	
	/**
	 * Creates an editor and renderer for a JSpinner used with a ShoppingTableModel so the table knows what to do
	 * when a spinner changes value and how to draw the JSpinner in the table cell.
	 */
	private class SpinnerEditorRenderer extends AbstractCellEditor
										implements TableCellEditor,
										TableCellRenderer{
		private static final long serialVersionUID = -9060125710311234974L;
		JSpinner spinner;
		
		public SpinnerEditorRenderer() {
			SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0,0,99,1);
			spinner = new JSpinner(spinnerModel);
		}
		
		public Object getCellEditorValue() {
			return spinner.getValue();
		}
		
		public Component getTableCellEditorComponent(JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
			return (JSpinner)shoppingTableModel.getValueAt(row,column);
		}
		
		public boolean isCellEditable(EventObject e) {
			return true;
		}
		
		public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
			return (JSpinner)shoppingTableModel.getValueAt(row,column);
		}
	}
	
	/**
	 * Creates a renderer for a table which will convert a string which contains a number into a currency format.
	 */
	private class PriceCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
			return new JLabel(currencyFormatter.format(Double.parseDouble((String)value)));
		}
	}
}
