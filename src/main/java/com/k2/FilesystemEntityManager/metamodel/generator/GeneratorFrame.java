package com.k2.FilesystemEntityManager.metamodel.generator;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.metamodel.ManagedType;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.k2.FilesystemEntityManager.metamodel.FemMetamodel;
import com.k2.Util.classes.ClassUtil;

import ch.qos.logback.core.util.FileUtil;

public class GeneratorFrame extends JFrame {

	/**
	 * http://zetcode.com/tutorials/javaswingtutorial/menusandtoolbars/
	 */
	private static final long serialVersionUID = 6103258998927431863L;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public GeneratorFrame() {
		initUI();
	}
	
	private class PackageName extends JPanel implements DocumentListener {
		
		String packageName;
		
		PackageName() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createRigidArea(new Dimension(5, 0)));
			add(new JLabel("Package name"));
			add(Box.createRigidArea(new Dimension(11, 0)));
			
			JTextField field = new JTextField(50);
			field.getDocument().addDocumentListener(this);
			add(field);
			
			add(Box.createRigidArea(new Dimension(5, 0)));
			
		}
		
		public String getPackageName() {
			return packageName;
		}
		
		private void setPackageName(DocumentEvent e) {
			Document doc = e.getDocument();
            int len = doc.getLength();

            try {
            	packageName = doc.getText(0, len);
            } catch (BadLocationException ex) {
            		logger.error("Error setting package name: {}", ex.getMessage());
            }
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			setPackageName(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			setPackageName(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {}
	}
	
	private class ExportLocation extends JPanel implements DocumentListener {
		
		String exportLocation;
		
		ExportLocation() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createRigidArea(new Dimension(5, 0)));
			add(new JLabel("Export location"));
			add(Box.createRigidArea(new Dimension(5, 0)));
			
			JTextField field = new JTextField(50);
			field.getDocument().addDocumentListener(this);
			add(field);
			
			add(Box.createRigidArea(new Dimension(5, 0)));
			
			JButton choose = new JButton("Choose");
			choose.addActionListener((ActionEvent event) -> {
				JFileChooser chooser = new JFileChooser();
				
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose the export directory");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	                File file = chooser.getSelectedFile();
	                field.setText(file.getPath().replace("./", ""));
	            }				
			});
			
			add(choose);
			add(Box.createRigidArea(new Dimension(5, 0)));
				
			
		}
		
		public String getExportLocation() {
			return exportLocation;
		}
		
		private void setPackageName(DocumentEvent e) {
			Document doc = e.getDocument();
            int len = doc.getLength();

            try {
            	exportLocation = doc.getText(0, len);
            } catch (BadLocationException ex) {
            		logger.error("Error setting export location: {}", ex.getMessage());
            }
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			setPackageName(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			setPackageName(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {}
	}
	
	private PackageName packageName = new PackageName();
	private ExportLocation exportLocation = new ExportLocation();
	
	private void initUI() {
		
		createMenuBar();
		
		setTitle("Static Metatmodel Generator");
		setSize(600, 180);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel basic = new JPanel();
		basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
        add(basic);
                
        basic.add(Box.createRigidArea(new Dimension(0, 15)));
        basic.add(packageName);
        
        basic.add(Box.createRigidArea(new Dimension(0, 15)));
        basic.add(exportLocation);
                
        basic.add(Box.createVerticalGlue());
        
        JPanel bottom = new JPanel();
        bottom.setAlignmentX(1f);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setToolTipText("Generate the @StaticMetamodel for the identified classes");
		btnGenerate.addActionListener((ActionEvent event) -> {
			logger.info("Generate! {} in {}", packageName.getPackageName(), exportLocation.getExportLocation() );
			
			FemMetamodel metamodel = new FemMetamodel(packageName.getPackageName());
			
			StaticMetamodelGenarator smg = new StaticMetamodelGenarator(metamodel);
			
			File exportRoot = new File(exportLocation.getExportLocation());
			
			logger.info("Generating static metamodel classes in {}", exportRoot.getAbsolutePath());
			
			for (ManagedType<?> mt : metamodel.getManagedTypes()) {
				File sourceFile = new File(exportRoot.getAbsoluteFile()+File.separator+ClassUtil.packageNameToPath(mt.getJavaType().getPackage().getName())+File.separator+mt.getJavaType().getSimpleName()+"_.java");
				
				logger.info("Generating static metamodel source for {} in {}", mt.getJavaType(), sourceFile.getAbsolutePath());
				Writer w = null;
				try {
					if (sourceFile.exists()) {
						Files.move(sourceFile, new File(sourceFile.getAbsolutePath()+".old"));
					}
					FileUtil.createMissingParentDirectories(sourceFile);
					sourceFile.createNewFile();
					w = Files.newWriter(sourceFile, Charset.forName("UTF8"));
					smg.generateStaticMetamodelSource(w, mt.getJavaType()).flush();;
					w.close();
					logger.info("Written {}", sourceFile.getAbsolutePath());
				} catch (IOException e) {
					logger.error("Failed to generate static metamodel class for {}", e, mt.getJavaType().getName());
				}
			}
		});
		
		bottom.add(btnGenerate);
		bottom.add(Box.createRigidArea(new Dimension(5, 0)));
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.setToolTipText("Quit the Static Metamodel Generator");
		btnQuit.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});
		
		bottom.add(btnQuit);
		bottom.add(Box.createRigidArea(new Dimension(15, 0)));
		
		basic.add(bottom);
        basic.add(Box.createRigidArea(new Dimension(0, 15)));

	}
	
    private void createMenuBar() {

        JMenuBar menubar = new JMenuBar();
 
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem eMenuItem = new JMenuItem("Exit");
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener((ActionEvent event) -> {
            System.exit(0);
        });

        file.add(eMenuItem);

        menubar.add(file);

        setJMenuBar(menubar);
    }

	private void createLayout (JComponent ...components) {
		Container pane = getContentPane();
		GroupLayout gl = new GroupLayout(pane);
		pane.setLayout(gl);
		
		gl.setAutoCreateContainerGaps(true);
		
		gl.setHorizontalGroup(gl.createSequentialGroup()
				.addComponent(components[0])
				.addComponent(components[1])
        );
		
		gl.setVerticalGroup(gl.createParallelGroup()
                .addComponent(components[0])
				.addComponent(components[1])
        );
		
		pack();
	}
	
	public static void main (String[] args) {
		EventQueue.invokeLater(() -> {
			GeneratorFrame gf = new GeneratorFrame();
			gf.setVisible(true);
		});
	}
}
