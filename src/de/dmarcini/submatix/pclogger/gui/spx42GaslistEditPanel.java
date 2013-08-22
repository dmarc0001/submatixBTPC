package de.dmarcini.submatix.pclogger.gui;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.GasComputeUnit;
import de.dmarcini.submatix.pclogger.utils.GasPresetComboBoxModel;
import de.dmarcini.submatix.pclogger.utils.GasPresetComboObject;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.SPX42GasList;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

//@formatter:off
/**
 * Editor für Gaslisten auf dem SPX
 *
 * @author Dirk Marciniak 22.08.2013
 */
public class spx42GaslistEditPanel extends JPanel implements ItemListener, ActionListener, ChangeListener
{  //
  private final HashMap<Integer, JSpinner>  o2SpinnerMap        = new HashMap<Integer, JSpinner>();
  private final HashMap<Integer, JSpinner>  heSpinnerMap        = new HashMap<Integer, JSpinner>();
  private final HashMap<Integer, JLabel>    gasLblMap           = new HashMap<Integer, JLabel>();
  private final HashMap<Integer, JLabel>    gasLblMap2          = new HashMap<Integer, JLabel>();
  private final HashMap<Integer, JCheckBox> bailoutMap          = new HashMap<Integer, JCheckBox>();
  private final HashMap<Integer, JCheckBox> diluent1Map         = new HashMap<Integer, JCheckBox>();
  private final HashMap<Integer, JCheckBox> diluent2Map         = new HashMap<Integer, JCheckBox>();
  private LogDerbyDatabaseUtil              databaseUtil        = null;
  private Logger                            lg                  = null;
  private int                               licenseState        = ProjectConst.SPX_LICENSE_NOT_SET;
  private int                               customConfig        = -1;
  private boolean                           isPanelInitiated    = false;
  private boolean                           isOnline            = false;
  private ResourceBundle                    stringsBundle       = null;
  private MainCommGUI                       mainCommGUI         = null;
  // private boolean isElementsGasMatrixEnabled = false;
  private SpxPcloggerProgramConfig          progConfig          = null;
  private String                            unitsString         = "metric";
  private double                            ppOMax              = 1.6D;
  private boolean                           salnity             = false;
  private SPX42GasList                      currGasList         = null;
  private boolean                           ignoreAction        = false;
  private static final Pattern              fieldPatternDp      = Pattern.compile(":");
  // @formatter:on
  /**
   * 
   */
  private static final long                 serialVersionUID    = 1L;
  private JLabel                            gasLabel_00;
  private JLabel                            gasLabel_01;
  private JLabel                            gasLabel_03;
  private JSpinner                          gasO2Spinner_00;
  private JSpinner                          gasHESpinner_00;
  private JCheckBox                         diluent1Checkbox_02;
  private JCheckBox                         diluent1Checkbox_00;
  private JCheckBox                         diluent1Checkbox_01;
  private JCheckBox                         diluent1Checkbox_03;
  private JCheckBox                         diluent1Checkbox_04;
  private JCheckBox                         diluent1Checkbox_05;
  private JCheckBox                         diluent1Checkbox_06;
  private JCheckBox                         diluent1Checkbox_07;
  private JCheckBox                         diluent2Checkbox_00;
  private JCheckBox                         diluent2Checkbox_01;
  private JCheckBox                         diluent2Checkbox_02;
  private JCheckBox                         diluent2Checkbox_03;
  private JCheckBox                         diluent2Checkbox_04;
  private JCheckBox                         diluent2Checkbox_05;
  private JCheckBox                         diluent2Checkbox_06;
  private JCheckBox                         diluent2Checkbox_07;
  private JCheckBox                         bailoutCheckbox_00;
  private JCheckBox                         bailoutCheckbox_01;
  private JCheckBox                         bailoutCheckbox_02;
  private JCheckBox                         bailoutCheckbox_03;
  private JCheckBox                         bailoutCheckbox_04;
  private JCheckBox                         bailoutCheckbox_05;
  private JCheckBox                         bailoutCheckbox_06;
  private JCheckBox                         bailoutCheckbox_07;
  private JLabel                            gasNameLabel_00;
  private JLabel                            gasNameLabel_01;
  private JLabel                            gasNameLabel_02;
  private JLabel                            gasNameLabel_04;
  private JLabel                            gasNameLabel_05;
  private JLabel                            gasNameLabel_06;
  private JLabel                            gasNameLabel_07;
  private final ButtonGroup                 duluent1ButtonGroup = new ButtonGroup();
  private final ButtonGroup                 diluent2ButtonGroup = new ButtonGroup();
  private JLabel                            licenseStatusLabel;
  private JButton                           gasReadFromSPXButton;
  private JButton                           gasWriteToSPXButton;
  private JSpinner                          gasO2Spinner_01;
  private JSpinner                          gasO2Spinner_02;
  private JSpinner                          gasO2Spinner_03;
  private JSpinner                          gasO2Spinner_04;
  private JSpinner                          gasO2Spinner_05;
  private JSpinner                          gasO2Spinner_06;
  private JSpinner                          gasO2Spinner_07;
  private JSpinner                          gasHESpinner_01;
  private JSpinner                          gasHESpinner_03;
  private JSpinner                          gasHESpinner_04;
  private JSpinner                          gasHESpinner_05;
  private JSpinner                          gasHESpinner_06;
  private JSpinner                          gasHESpinner_07;
  private JSpinner                          gasHESpinner_02;
  private JLabel                            gasNameLabel_03;
  private JLabel                            gasLabel_02;
  private JLabel                            gasLabel_04;
  private JLabel                            gasLabel_05;
  private JLabel                            gasLabel_06;
  private JLabel                            gasLabel_07;
  private JButton                           writeGasPresetButton;
  private JComboBox                         customPresetComboBox;
  private JPanel                            gasMatrixPanel;
  private JLabel                            pressureUnitLabel;
  private JComboBox                         ppoMaxComboBox;
  private JCheckBox                         salnityCheckBox;
  private JLabel                            borderGasLabel_00;
  private JLabel                            borderGasLabel_01;
  private JLabel                            borderGasLabel_02;
  private JLabel                            borderGasLabel_03;
  private JLabel                            borderGasLabel_04;
  private JLabel                            borderGasLabel_05;
  private JLabel                            borderGasLabel_06;
  private JLabel                            borderGasLabel_07;
  private JButton                           buttonComputeGases;

  /**
   * gesperrte Version
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 21.04.2012
   */
  @SuppressWarnings("unused")
  private spx42GaslistEditPanel()
  {
    setPreferredSize(new Dimension(796, 504));
    isPanelInitiated = false;
    initPanel();
    initGasObjectMaps();
  }

  /**
   * Mache das Panel!
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 21.04.2012
   * @param logger
   * @param databaseUtil
   * @param progConfig
   */
  public spx42GaslistEditPanel(final LogDerbyDatabaseUtil databaseUtil, final SpxPcloggerProgramConfig progConfig)
  {
    lg = SpxPcloggerProgramConfig.LOGGER;
    if (lg == null) { throw new NullPointerException("no logger in constructor!"); }
    this.progConfig = progConfig;
    this.databaseUtil = databaseUtil;
    this.currGasList = new SPX42GasList();
    this.isPanelInitiated = false;
    this.isOnline = false;
    this.licenseState = ProjectConst.SPX_LICENSE_NOT_SET;
  }

  @Override
  public void actionPerformed(ActionEvent ev)
  {
    // /////////////////////////////////////////////////////////////////////////
    // Combobox
    if (ev.getSource() instanceof JComboBox)
    {
      JComboBox cb = (JComboBox) ev.getSource();
      if (ev.getActionCommand().equals("set_ppomax"))
      {
        try
        {
          ppOMax = Double.parseDouble((String) (cb.getModel().getElementAt(cb.getSelectedIndex())));
          lg.debug(String.format("ppoMax set to %2.2f", ppOMax));
          setAllDescriptionsForGas();
        }
        catch (NumberFormatException ex)
        {
          lg.error(ex.getLocalizedMessage());
          ppOMax = 1.6D;
        }
      }
      else
      {
        lg.warn("unknown combobox action event <" + ev.getActionCommand() + ">");
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // Button
    else if (ev.getSource() instanceof JButton)
    {
      // es war ein Button
      // mach was daraus
      JButton bt = (JButton) ev.getSource();
      String cmd = bt.getActionCommand();
      // ///////////////////////////////////////////////////////////////////////
      // Gas als Preset schreiben
      if (cmd.equals("write_gas_preset"))
      {
        // ist es ein update oder solls ein neues Teil werden
        if ((customPresetComboBox.getSelectedIndex() == 0) || (customPresetComboBox.getSelectedIndex() == -1))
        {
          // auf jeden Fall NEU
          lg.debug("create a new gas preset...");
          showPresetSaveEditForm("NEW", -1);
          fillPresetComboBox();
        }
        else
        {
          lg.debug("update an exist gas preset...");
          GasPresetComboBoxModel cbm = (GasPresetComboBoxModel) customPresetComboBox.getModel();
          int idx = customPresetComboBox.getSelectedIndex();
          // ein Update machen
          showPresetSaveEditForm(cbm.getNameAt(idx), cbm.getDatabaseIdAt(idx));
          fillPresetComboBox();
          customPresetComboBox.setSelectedIndex(idx);
        }
      }
      // ///////////////////////////////////////////////////////////////////////
      // Gas vom SPX lesen
      else if (cmd.equals("read_gaslist"))
      {
        // will vom SPX lesen, dann muss die combobox aber auf Index "0"
        customPresetComboBox.setSelectedIndex(0);
      }
      // ///////////////////////////////////////////////////////////////////////
      // Gasbrerchnungen machen
      else if (cmd.equals("compute_gas"))
      {
        //
        // öffne einen Berechnungsdialog zur Berechnung von Gasen
        //
        GasComputeDialog gcDial = new GasComputeDialog(stringsBundle, unitsString);
        gcDial.showModal();
        //
        // hier könnte ich noch was machen
        //
        gcDial.dispose();
      }
      else
      {
        lg.warn("unknown button action event <" + ev.getActionCommand() + ">");
      }
    }
    else
    {
      lg.warn("unknown action event <" + ev.getActionCommand() + ">");
    }
  }

  /**
   * Ändere Heliumanteil vom Gas Nummer X Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.04.2012
   * @param gasNr
   *          welches Gas denn
   * @param he
   *          Heliumanteil
   */
  private void changeHEFromGas(int gasNr, int he)
  {
    int o2;
    o2 = currGasList.getO2FromGas(gasNr);
    ignoreAction = true;
    if (heSpinnerMap == null) return;
    if (o2SpinnerMap == null) return;
    if (he < 0)
    {
      he = 0;
      (heSpinnerMap.get(gasNr)).setValue(0);
    }
    else if (he > 100)
    {
      // Mehr als 100% geht nicht!
      // ungesundes Zeug!
      o2 = 0;
      he = 100;
      (heSpinnerMap.get(gasNr)).setValue(he);
      (o2SpinnerMap.get(gasNr)).setValue(o2);
      lg.warn(String.format("change helium (max) in Gas %d Value: <%d/0x%02x>...", gasNr, he, he));
    }
    else if ((o2 + he) > 100)
    {
      // Auch hier geht nicht mehr als 100%
      // Sauerstoff verringern!
      o2 = 100 - he;
      (o2SpinnerMap.get(gasNr)).setValue(o2);
      lg.debug(String.format("change helium in Gas %d Value: <%d/0x%02x>, reduct O2 <%d/0x%02x...", gasNr, he, he, o2, o2));
    }
    else
    {
      lg.debug(String.format("change helium in Gas %d Value: <%d/0x%02x> O2: <%d/0x%02x>...", gasNr, he, he, o2, o2));
    }
    currGasList.setGas(gasNr, o2, he);
    setDescriptionForGas(gasNr, o2, he);
    ignoreAction = false;
  }

  /**
   * Ändere Sauerstoffanteil vom Gas Nummer X Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.04.2012
   * @param gasNr
   *          welches Gas
   * @param o2
   *          Sauerstoffanteil
   */
  private void changeO2FromGas(int gasNr, int o2)
  {
    int he;
    he = currGasList.getHEFromGas(gasNr);
    ignoreAction = true;
    if (heSpinnerMap == null) return;
    if (o2SpinnerMap == null) return;
    if (o2 < 0)
    {
      // das Zeut ist dann auch ungesund!
      o2 = 0;
      (o2SpinnerMap.get(gasNr)).setValue(0);
    }
    else if (o2 > 100)
    {
      // Mehr als 100% geht nicht!
      o2 = 100;
      he = 0;
      (heSpinnerMap.get(gasNr)).setValue(he);
      (o2SpinnerMap.get(gasNr)).setValue(o2);
      lg.warn(String.format("change oxygen (max) in Gas %d Value: <%d/0x%02x>...", gasNr, o2, o2));
    }
    else if ((o2 + he) > 100)
    {
      // Auch hier geht nicht mehr als 100%
      // Helium verringern!
      he = 100 - o2;
      (heSpinnerMap.get(gasNr)).setValue(he);
      lg.debug(String.format("change oxygen in Gas %d Value: <%d/0x%02x>, reduct HE <%d/0x%02x...", gasNr, o2, o2, he, he));
    }
    else
    {
      lg.debug(String.format("change oxygen in Gas %d Value: <%d/0x%02x>...", gasNr, o2, o2));
    }
    currGasList.setGas(gasNr, o2, he);
    // erzeuge und setze noch den Gasnamen
    // und färbe dabei gleich die Zahlen ein
    setDescriptionForGas(gasNr, o2, he);
    ignoreAction = false;
  }

  /**
   * Fülle die Preset-Combobox mit Daten aus der Datenbank
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 23.09.2012
   */
  private void fillPresetComboBox()
  {
    // GasPresetComboBoxModel
    if (databaseUtil == null) return;
    if (!databaseUtil.isOpenDB())
    {
      lg.error("database is not OPENED!");
      return;
    }
    //
    // jetzt frag mal die DB nach den Daten
    //
    GasPresetComboBoxModel presetModel = new GasPresetComboBoxModel(databaseUtil.getPresets());
    presetModel.insertElementAt(new GasPresetComboObject("SPX42 - current", -1), 0);
    customPresetComboBox.setModel(presetModel);
    if (isOnline)
    {
      customPresetComboBox.setSelectedIndex(0);
    }
    else
    {
      customPresetComboBox.setSelectedIndex(0);
    }
  }

  /**
   * Die aktuelle Gasliste zurückgeben
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 07.10.2012
   * @return aktuelle Online Gasliste
   */
  public SPX42GasList getCurrGasList()
  {
    return (currGasList);
  }

  /**
   * Gase von currentGas initialisieren
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 07.10.2012
   */
  public void initGasesFromCurrent()
  {
    //
    // Gase initialisieren
    //
    if (!currGasList.isInitialized())
    {
      lg.warn("currentGasList nor initialized! Abort.");
      return;
    }
    //
    // erst mal nicht auf Änderugen reagieren
    //
    ignoreAction = true;
    //
    // alle Gase eintragen
    //
    for (int idx = 0; idx < currGasList.getGasCount(); idx++)
    {
      (heSpinnerMap.get(idx)).setValue(currGasList.getHEFromGas(idx));
      (o2SpinnerMap.get(idx)).setValue(currGasList.getO2FromGas(idx));
      if (currGasList.getDiulent1() == idx)
      {
        (diluent1Map.get(idx)).setSelected(true);
      }
      // ist dieses Gas Diluent 2?
      if (currGasList.getDiluent2() == idx)
      {
        (diluent2Map.get(idx)).setSelected(true);
      }
      // Status als Bailoutgas?
      if (currGasList.getBailout(idx) == 3)
      {
        (bailoutMap.get(idx)).setSelected(true);
      }
      else
      {
        (bailoutMap.get(idx)).setSelected(false);
      }
    }
    //
    // nachträglich alle Beschreibungen setzen
    //
    setAllDescriptionsForGas();
    //
    // reagiere wieder auf Änderungen
    //
    ignoreAction = false;
  }

  /**
   * Für unkomplizierteren Zugriff die Objekte Indizieren
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 18.04.2012
   */
  private void initGasObjectMaps()
  {
    o2SpinnerMap.put(0, gasO2Spinner_00);
    o2SpinnerMap.put(1, gasO2Spinner_01);
    o2SpinnerMap.put(2, gasO2Spinner_02);
    o2SpinnerMap.put(3, gasO2Spinner_03);
    o2SpinnerMap.put(4, gasO2Spinner_04);
    o2SpinnerMap.put(5, gasO2Spinner_05);
    o2SpinnerMap.put(6, gasO2Spinner_06);
    o2SpinnerMap.put(7, gasO2Spinner_07);
    //
    heSpinnerMap.put(0, gasHESpinner_00);
    heSpinnerMap.put(1, gasHESpinner_01);
    heSpinnerMap.put(2, gasHESpinner_02);
    heSpinnerMap.put(3, gasHESpinner_03);
    heSpinnerMap.put(4, gasHESpinner_04);
    heSpinnerMap.put(5, gasHESpinner_05);
    heSpinnerMap.put(6, gasHESpinner_06);
    heSpinnerMap.put(7, gasHESpinner_07);
    //
    gasLblMap.put(0, gasNameLabel_00);
    gasLblMap.put(1, gasNameLabel_01);
    gasLblMap.put(2, gasNameLabel_02);
    gasLblMap.put(3, gasNameLabel_03);
    gasLblMap.put(4, gasNameLabel_04);
    gasLblMap.put(5, gasNameLabel_05);
    gasLblMap.put(6, gasNameLabel_06);
    gasLblMap.put(7, gasNameLabel_07);
    //
    gasLblMap2.put(0, borderGasLabel_00);
    gasLblMap2.put(1, borderGasLabel_01);
    gasLblMap2.put(2, borderGasLabel_02);
    gasLblMap2.put(3, borderGasLabel_03);
    gasLblMap2.put(4, borderGasLabel_04);
    gasLblMap2.put(5, borderGasLabel_05);
    gasLblMap2.put(6, borderGasLabel_06);
    gasLblMap2.put(7, borderGasLabel_07);
    //
    bailoutMap.put(0, bailoutCheckbox_00);
    bailoutMap.put(1, bailoutCheckbox_01);
    bailoutMap.put(2, bailoutCheckbox_02);
    bailoutMap.put(3, bailoutCheckbox_03);
    bailoutMap.put(4, bailoutCheckbox_04);
    bailoutMap.put(5, bailoutCheckbox_05);
    bailoutMap.put(6, bailoutCheckbox_06);
    bailoutMap.put(7, bailoutCheckbox_07);
    //
    diluent1Map.put(0, diluent1Checkbox_00);
    diluent1Map.put(1, diluent1Checkbox_01);
    diluent1Map.put(2, diluent1Checkbox_02);
    diluent1Map.put(3, diluent1Checkbox_03);
    diluent1Map.put(4, diluent1Checkbox_04);
    diluent1Map.put(5, diluent1Checkbox_05);
    diluent1Map.put(6, diluent1Checkbox_06);
    diluent1Map.put(7, diluent1Checkbox_07);
    //
    diluent2Map.put(0, diluent2Checkbox_00);
    diluent2Map.put(1, diluent2Checkbox_01);
    diluent2Map.put(2, diluent2Checkbox_02);
    diluent2Map.put(3, diluent2Checkbox_03);
    diluent2Map.put(4, diluent2Checkbox_04);
    diluent2Map.put(5, diluent2Checkbox_05);
    diluent2Map.put(6, diluent2Checkbox_06);
    diluent2Map.put(7, diluent2Checkbox_07);
  }

  /**
   * Interne Funktion erzeugt die GUI
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 02.09.2012
   */
  private void initPanel()
  {
    setLayout(null);
    gasReadFromSPXButton = new JButton("READ");
    gasReadFromSPXButton.setHorizontalAlignment(SwingConstants.LEFT);
    gasReadFromSPXButton.setIconTextGap(15);
    gasReadFromSPXButton.setIcon(new ImageIcon(spx42GaslistEditPanel.class.getResource("/de/dmarcini/submatix/pclogger/res/Download.png")));
    gasReadFromSPXButton.setSize(new Dimension(199, 60));
    gasReadFromSPXButton.setPreferredSize(new Dimension(180, 40));
    gasReadFromSPXButton.setMaximumSize(new Dimension(160, 40));
    gasReadFromSPXButton.setMargin(new Insets(2, 30, 2, 30));
    gasReadFromSPXButton.setForeground(new Color(0, 100, 0));
    gasReadFromSPXButton.setBackground(new Color(152, 251, 152));
    gasReadFromSPXButton.setActionCommand("read_gaslist");
    gasReadFromSPXButton.setBounds(10, 421, 200, 60);
    add(gasReadFromSPXButton);
    gasWriteToSPXButton = new JButton("WRITE");
    gasWriteToSPXButton.setIconTextGap(15);
    gasWriteToSPXButton.setHorizontalAlignment(SwingConstants.LEFT);
    gasWriteToSPXButton.setIcon(new ImageIcon(spx42GaslistEditPanel.class.getResource("/de/dmarcini/submatix/pclogger/res/Upload.png")));
    gasWriteToSPXButton.setForeground(Color.RED);
    gasWriteToSPXButton.setBackground(new Color(255, 192, 203));
    gasWriteToSPXButton.setActionCommand("write_gaslist");
    gasWriteToSPXButton.setBounds(219, 421, 200, 60);
    add(gasWriteToSPXButton);
    gasMatrixPanel = new JPanel();
    gasMatrixPanel.setBorder(new LineBorder(Color.BLACK, 1, true));
    gasMatrixPanel.setBounds(10, 11, 773, 368);
    add(gasMatrixPanel);
    GridBagLayout gbl_gasMatrixPanel = new GridBagLayout();
    gbl_gasMatrixPanel.columnWidths = new int[] { 16, 70, 0, 50, 0, 50, 0, 55, 55, 55, 60, 245, 0 };
    gbl_gasMatrixPanel.rowHeights = new int[] { 37, 40, 40, 40, 40, 40, 40, 40, 40, 0 };
    gbl_gasMatrixPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_gasMatrixPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gasMatrixPanel.setLayout(gbl_gasMatrixPanel);
    JLabel label = new JLabel("O2");
    label.setFont(new Font("Tahoma", Font.BOLD, 12));
    GridBagConstraints gbc_label = new GridBagConstraints();
    gbc_label.anchor = GridBagConstraints.SOUTH;
    gbc_label.insets = new Insets(0, 0, 5, 5);
    gbc_label.gridx = 3;
    gbc_label.gridy = 0;
    gasMatrixPanel.add(label, gbc_label);
    JLabel label_1 = new JLabel("HE");
    label_1.setFont(new Font("Tahoma", Font.BOLD, 12));
    GridBagConstraints gbc_label_1 = new GridBagConstraints();
    gbc_label_1.anchor = GridBagConstraints.SOUTH;
    gbc_label_1.insets = new Insets(0, 0, 5, 5);
    gbc_label_1.gridx = 5;
    gbc_label_1.gridy = 0;
    gasMatrixPanel.add(label_1, gbc_label_1);
    gasLabel_00 = new JLabel("GAS00");
    GridBagConstraints gbc_gasLabel_00 = new GridBagConstraints();
    gbc_gasLabel_00.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_00.insets = new Insets(0, 0, 5, 5);
    gbc_gasLabel_00.gridx = 1;
    gbc_gasLabel_00.gridy = 1;
    gasMatrixPanel.add(gasLabel_00, gbc_gasLabel_00);
    gasO2Spinner_00 = new JSpinner();
    gasO2Spinner_00.setBounds(new Rectangle(0, 0, 50, 0));
    gasO2Spinner_00.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasO2Spinner_00 = new GridBagConstraints();
    gbc_gasO2Spinner_00.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_00.insets = new Insets(0, 0, 5, 5);
    gbc_gasO2Spinner_00.gridx = 3;
    gbc_gasO2Spinner_00.gridy = 1;
    gasMatrixPanel.add(gasO2Spinner_00, gbc_gasO2Spinner_00);
    gasHESpinner_00 = new JSpinner();
    gasHESpinner_00.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasHESpinner_00 = new GridBagConstraints();
    gbc_gasHESpinner_00.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_00.insets = new Insets(0, 0, 5, 5);
    gbc_gasHESpinner_00.gridx = 5;
    gbc_gasHESpinner_00.gridy = 1;
    gasMatrixPanel.add(gasHESpinner_00, gbc_gasHESpinner_00);
    diluent1Checkbox_00 = new JCheckBox("D1");
    duluent1ButtonGroup.add(diluent1Checkbox_00);
    GridBagConstraints gbc_diluent1Checkbox_00 = new GridBagConstraints();
    gbc_diluent1Checkbox_00.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_00.insets = new Insets(0, 0, 5, 5);
    gbc_diluent1Checkbox_00.gridx = 7;
    gbc_diluent1Checkbox_00.gridy = 1;
    gasMatrixPanel.add(diluent1Checkbox_00, gbc_diluent1Checkbox_00);
    diluent2Checkbox_00 = new JCheckBox("D2");
    diluent2ButtonGroup.add(diluent2Checkbox_00);
    GridBagConstraints gbc_diluent2Checkbox_00 = new GridBagConstraints();
    gbc_diluent2Checkbox_00.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_00.insets = new Insets(0, 0, 5, 5);
    gbc_diluent2Checkbox_00.gridx = 8;
    gbc_diluent2Checkbox_00.gridy = 1;
    gasMatrixPanel.add(diluent2Checkbox_00, gbc_diluent2Checkbox_00);
    bailoutCheckbox_00 = new JCheckBox("B");
    GridBagConstraints gbc_bailoutCheckbox_00 = new GridBagConstraints();
    gbc_bailoutCheckbox_00.insets = new Insets(0, 0, 5, 5);
    gbc_bailoutCheckbox_00.gridx = 9;
    gbc_bailoutCheckbox_00.gridy = 1;
    gasMatrixPanel.add(bailoutCheckbox_00, gbc_bailoutCheckbox_00);
    gasNameLabel_00 = new JLabel("AIR");
    gasNameLabel_00.setForeground(new Color(0, 0, 128));
    gasNameLabel_00.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_gasNameLabel_00 = new GridBagConstraints();
    gbc_gasNameLabel_00.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_00.insets = new Insets(0, 0, 5, 5);
    gbc_gasNameLabel_00.gridx = 10;
    gbc_gasNameLabel_00.gridy = 1;
    gasMatrixPanel.add(gasNameLabel_00, gbc_gasNameLabel_00);
    borderGasLabel_00 = new JLabel("-");
    borderGasLabel_00.setForeground(Color.BLUE);
    borderGasLabel_00.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_borderGasLabel_00 = new GridBagConstraints();
    gbc_borderGasLabel_00.anchor = GridBagConstraints.WEST;
    gbc_borderGasLabel_00.insets = new Insets(0, 0, 5, 0);
    gbc_borderGasLabel_00.gridx = 11;
    gbc_borderGasLabel_00.gridy = 1;
    gasMatrixPanel.add(borderGasLabel_00, gbc_borderGasLabel_00);
    gasLabel_01 = new JLabel("GAS01");
    GridBagConstraints gbc_gasLabel_01 = new GridBagConstraints();
    gbc_gasLabel_01.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_01.insets = new Insets(0, 0, 5, 5);
    gbc_gasLabel_01.gridx = 1;
    gbc_gasLabel_01.gridy = 2;
    gasMatrixPanel.add(gasLabel_01, gbc_gasLabel_01);
    gasO2Spinner_01 = new JSpinner();
    gasO2Spinner_01.setBounds(new Rectangle(0, 0, 50, 0));
    gasO2Spinner_01.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasO2Spinner_01 = new GridBagConstraints();
    gbc_gasO2Spinner_01.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_01.insets = new Insets(0, 0, 5, 5);
    gbc_gasO2Spinner_01.gridx = 3;
    gbc_gasO2Spinner_01.gridy = 2;
    gasMatrixPanel.add(gasO2Spinner_01, gbc_gasO2Spinner_01);
    gasHESpinner_01 = new JSpinner();
    gasHESpinner_01.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasHESpinner_01 = new GridBagConstraints();
    gbc_gasHESpinner_01.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_01.insets = new Insets(0, 0, 5, 5);
    gbc_gasHESpinner_01.gridx = 5;
    gbc_gasHESpinner_01.gridy = 2;
    gasMatrixPanel.add(gasHESpinner_01, gbc_gasHESpinner_01);
    diluent1Checkbox_01 = new JCheckBox("D1");
    duluent1ButtonGroup.add(diluent1Checkbox_01);
    GridBagConstraints gbc_diluent1Checkbox_01 = new GridBagConstraints();
    gbc_diluent1Checkbox_01.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_01.insets = new Insets(0, 0, 5, 5);
    gbc_diluent1Checkbox_01.gridx = 7;
    gbc_diluent1Checkbox_01.gridy = 2;
    gasMatrixPanel.add(diluent1Checkbox_01, gbc_diluent1Checkbox_01);
    diluent2Checkbox_01 = new JCheckBox("D2");
    diluent2ButtonGroup.add(diluent2Checkbox_01);
    GridBagConstraints gbc_diluent2Checkbox_01 = new GridBagConstraints();
    gbc_diluent2Checkbox_01.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_01.insets = new Insets(0, 0, 5, 5);
    gbc_diluent2Checkbox_01.gridx = 8;
    gbc_diluent2Checkbox_01.gridy = 2;
    gasMatrixPanel.add(diluent2Checkbox_01, gbc_diluent2Checkbox_01);
    bailoutCheckbox_01 = new JCheckBox("B");
    GridBagConstraints gbc_bailoutCheckbox_01 = new GridBagConstraints();
    gbc_bailoutCheckbox_01.insets = new Insets(0, 0, 5, 5);
    gbc_bailoutCheckbox_01.gridx = 9;
    gbc_bailoutCheckbox_01.gridy = 2;
    gasMatrixPanel.add(bailoutCheckbox_01, gbc_bailoutCheckbox_01);
    gasNameLabel_01 = new JLabel("AIR");
    gasNameLabel_01.setForeground(new Color(0, 0, 128));
    gasNameLabel_01.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_gasNameLabel_01 = new GridBagConstraints();
    gbc_gasNameLabel_01.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_01.insets = new Insets(0, 0, 5, 5);
    gbc_gasNameLabel_01.gridx = 10;
    gbc_gasNameLabel_01.gridy = 2;
    gasMatrixPanel.add(gasNameLabel_01, gbc_gasNameLabel_01);
    borderGasLabel_01 = new JLabel("-");
    borderGasLabel_01.setForeground(Color.BLUE);
    borderGasLabel_01.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_borderGasLabel_01 = new GridBagConstraints();
    gbc_borderGasLabel_01.anchor = GridBagConstraints.WEST;
    gbc_borderGasLabel_01.insets = new Insets(0, 0, 5, 0);
    gbc_borderGasLabel_01.gridx = 11;
    gbc_borderGasLabel_01.gridy = 2;
    gasMatrixPanel.add(borderGasLabel_01, gbc_borderGasLabel_01);
    gasLabel_02 = new JLabel("GAS02");
    GridBagConstraints gbc_gasLabel_02 = new GridBagConstraints();
    gbc_gasLabel_02.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_02.insets = new Insets(0, 0, 5, 5);
    gbc_gasLabel_02.gridx = 1;
    gbc_gasLabel_02.gridy = 3;
    gasMatrixPanel.add(gasLabel_02, gbc_gasLabel_02);
    gasO2Spinner_02 = new JSpinner();
    gasO2Spinner_02.setBounds(new Rectangle(0, 0, 50, 0));
    gasO2Spinner_02.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasO2Spinner_02 = new GridBagConstraints();
    gbc_gasO2Spinner_02.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_02.insets = new Insets(0, 0, 5, 5);
    gbc_gasO2Spinner_02.gridx = 3;
    gbc_gasO2Spinner_02.gridy = 3;
    gasMatrixPanel.add(gasO2Spinner_02, gbc_gasO2Spinner_02);
    gasHESpinner_02 = new JSpinner();
    gasHESpinner_02.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasHESpinner_02 = new GridBagConstraints();
    gbc_gasHESpinner_02.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_02.insets = new Insets(0, 0, 5, 5);
    gbc_gasHESpinner_02.gridx = 5;
    gbc_gasHESpinner_02.gridy = 3;
    gasMatrixPanel.add(gasHESpinner_02, gbc_gasHESpinner_02);
    diluent1Checkbox_02 = new JCheckBox("D1");
    duluent1ButtonGroup.add(diluent1Checkbox_02);
    GridBagConstraints gbc_diluent1Checkbox_02 = new GridBagConstraints();
    gbc_diluent1Checkbox_02.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_02.insets = new Insets(0, 0, 5, 5);
    gbc_diluent1Checkbox_02.gridx = 7;
    gbc_diluent1Checkbox_02.gridy = 3;
    gasMatrixPanel.add(diluent1Checkbox_02, gbc_diluent1Checkbox_02);
    diluent2Checkbox_02 = new JCheckBox("D2");
    diluent2ButtonGroup.add(diluent2Checkbox_02);
    GridBagConstraints gbc_diluent2Checkbox_02 = new GridBagConstraints();
    gbc_diluent2Checkbox_02.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_02.insets = new Insets(0, 0, 5, 5);
    gbc_diluent2Checkbox_02.gridx = 8;
    gbc_diluent2Checkbox_02.gridy = 3;
    gasMatrixPanel.add(diluent2Checkbox_02, gbc_diluent2Checkbox_02);
    bailoutCheckbox_02 = new JCheckBox("B");
    GridBagConstraints gbc_bailoutCheckbox_02 = new GridBagConstraints();
    gbc_bailoutCheckbox_02.insets = new Insets(0, 0, 5, 5);
    gbc_bailoutCheckbox_02.gridx = 9;
    gbc_bailoutCheckbox_02.gridy = 3;
    gasMatrixPanel.add(bailoutCheckbox_02, gbc_bailoutCheckbox_02);
    gasNameLabel_02 = new JLabel("AIR");
    gasNameLabel_02.setForeground(new Color(0, 0, 128));
    gasNameLabel_02.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_gasNameLabel_02 = new GridBagConstraints();
    gbc_gasNameLabel_02.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_02.insets = new Insets(0, 0, 5, 5);
    gbc_gasNameLabel_02.gridx = 10;
    gbc_gasNameLabel_02.gridy = 3;
    gasMatrixPanel.add(gasNameLabel_02, gbc_gasNameLabel_02);
    borderGasLabel_02 = new JLabel("-");
    borderGasLabel_02.setForeground(Color.BLUE);
    borderGasLabel_02.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_borderGasLabel_02 = new GridBagConstraints();
    gbc_borderGasLabel_02.anchor = GridBagConstraints.WEST;
    gbc_borderGasLabel_02.insets = new Insets(0, 0, 5, 0);
    gbc_borderGasLabel_02.gridx = 11;
    gbc_borderGasLabel_02.gridy = 3;
    gasMatrixPanel.add(borderGasLabel_02, gbc_borderGasLabel_02);
    gasLabel_03 = new JLabel("GAS03");
    GridBagConstraints gbc_gasLabel_03 = new GridBagConstraints();
    gbc_gasLabel_03.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_03.insets = new Insets(0, 0, 5, 5);
    gbc_gasLabel_03.gridx = 1;
    gbc_gasLabel_03.gridy = 4;
    gasMatrixPanel.add(gasLabel_03, gbc_gasLabel_03);
    gasO2Spinner_03 = new JSpinner();
    gasO2Spinner_03.setBounds(new Rectangle(0, 0, 50, 0));
    gasO2Spinner_03.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasO2Spinner_03 = new GridBagConstraints();
    gbc_gasO2Spinner_03.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_03.insets = new Insets(0, 0, 5, 5);
    gbc_gasO2Spinner_03.gridx = 3;
    gbc_gasO2Spinner_03.gridy = 4;
    gasMatrixPanel.add(gasO2Spinner_03, gbc_gasO2Spinner_03);
    gasHESpinner_03 = new JSpinner();
    gasHESpinner_03.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasHESpinner_03 = new GridBagConstraints();
    gbc_gasHESpinner_03.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_03.insets = new Insets(0, 0, 5, 5);
    gbc_gasHESpinner_03.gridx = 5;
    gbc_gasHESpinner_03.gridy = 4;
    gasMatrixPanel.add(gasHESpinner_03, gbc_gasHESpinner_03);
    diluent1Checkbox_03 = new JCheckBox("D1");
    duluent1ButtonGroup.add(diluent1Checkbox_03);
    GridBagConstraints gbc_diluent1Checkbox_03 = new GridBagConstraints();
    gbc_diluent1Checkbox_03.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_03.insets = new Insets(0, 0, 5, 5);
    gbc_diluent1Checkbox_03.gridx = 7;
    gbc_diluent1Checkbox_03.gridy = 4;
    gasMatrixPanel.add(diluent1Checkbox_03, gbc_diluent1Checkbox_03);
    diluent2Checkbox_03 = new JCheckBox("D2");
    diluent2ButtonGroup.add(diluent2Checkbox_03);
    GridBagConstraints gbc_diluent2Checkbox_03 = new GridBagConstraints();
    gbc_diluent2Checkbox_03.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_03.insets = new Insets(0, 0, 5, 5);
    gbc_diluent2Checkbox_03.gridx = 8;
    gbc_diluent2Checkbox_03.gridy = 4;
    gasMatrixPanel.add(diluent2Checkbox_03, gbc_diluent2Checkbox_03);
    bailoutCheckbox_03 = new JCheckBox("B");
    GridBagConstraints gbc_bailoutCheckbox_03 = new GridBagConstraints();
    gbc_bailoutCheckbox_03.insets = new Insets(0, 0, 5, 5);
    gbc_bailoutCheckbox_03.gridx = 9;
    gbc_bailoutCheckbox_03.gridy = 4;
    gasMatrixPanel.add(bailoutCheckbox_03, gbc_bailoutCheckbox_03);
    gasNameLabel_03 = new JLabel("AIR");
    gasNameLabel_03.setForeground(new Color(0, 0, 128));
    gasNameLabel_03.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_gasNameLabel_03 = new GridBagConstraints();
    gbc_gasNameLabel_03.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_03.insets = new Insets(0, 0, 5, 5);
    gbc_gasNameLabel_03.gridx = 10;
    gbc_gasNameLabel_03.gridy = 4;
    gasMatrixPanel.add(gasNameLabel_03, gbc_gasNameLabel_03);
    borderGasLabel_03 = new JLabel("-");
    borderGasLabel_03.setForeground(Color.BLUE);
    borderGasLabel_03.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_borderGasLabel_03 = new GridBagConstraints();
    gbc_borderGasLabel_03.anchor = GridBagConstraints.WEST;
    gbc_borderGasLabel_03.insets = new Insets(0, 0, 5, 0);
    gbc_borderGasLabel_03.gridx = 11;
    gbc_borderGasLabel_03.gridy = 4;
    gasMatrixPanel.add(borderGasLabel_03, gbc_borderGasLabel_03);
    gasLabel_04 = new JLabel("GAS04");
    GridBagConstraints gbc_gasLabel_04 = new GridBagConstraints();
    gbc_gasLabel_04.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_04.insets = new Insets(0, 0, 5, 5);
    gbc_gasLabel_04.gridx = 1;
    gbc_gasLabel_04.gridy = 5;
    gasMatrixPanel.add(gasLabel_04, gbc_gasLabel_04);
    gasO2Spinner_04 = new JSpinner();
    gasO2Spinner_04.setBounds(new Rectangle(0, 0, 50, 0));
    gasO2Spinner_04.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasO2Spinner_04 = new GridBagConstraints();
    gbc_gasO2Spinner_04.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_04.insets = new Insets(0, 0, 5, 5);
    gbc_gasO2Spinner_04.gridx = 3;
    gbc_gasO2Spinner_04.gridy = 5;
    gasMatrixPanel.add(gasO2Spinner_04, gbc_gasO2Spinner_04);
    gasHESpinner_04 = new JSpinner();
    gasHESpinner_04.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasHESpinner_04 = new GridBagConstraints();
    gbc_gasHESpinner_04.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_04.insets = new Insets(0, 0, 5, 5);
    gbc_gasHESpinner_04.gridx = 5;
    gbc_gasHESpinner_04.gridy = 5;
    gasMatrixPanel.add(gasHESpinner_04, gbc_gasHESpinner_04);
    diluent1Checkbox_04 = new JCheckBox("D1");
    duluent1ButtonGroup.add(diluent1Checkbox_04);
    GridBagConstraints gbc_diluent1Checkbox_04 = new GridBagConstraints();
    gbc_diluent1Checkbox_04.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_04.insets = new Insets(0, 0, 5, 5);
    gbc_diluent1Checkbox_04.gridx = 7;
    gbc_diluent1Checkbox_04.gridy = 5;
    gasMatrixPanel.add(diluent1Checkbox_04, gbc_diluent1Checkbox_04);
    diluent2Checkbox_04 = new JCheckBox("D2");
    diluent2ButtonGroup.add(diluent2Checkbox_04);
    GridBagConstraints gbc_diluent2Checkbox_04 = new GridBagConstraints();
    gbc_diluent2Checkbox_04.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_04.insets = new Insets(0, 0, 5, 5);
    gbc_diluent2Checkbox_04.gridx = 8;
    gbc_diluent2Checkbox_04.gridy = 5;
    gasMatrixPanel.add(diluent2Checkbox_04, gbc_diluent2Checkbox_04);
    bailoutCheckbox_04 = new JCheckBox("B");
    GridBagConstraints gbc_bailoutCheckbox_04 = new GridBagConstraints();
    gbc_bailoutCheckbox_04.insets = new Insets(0, 0, 5, 5);
    gbc_bailoutCheckbox_04.gridx = 9;
    gbc_bailoutCheckbox_04.gridy = 5;
    gasMatrixPanel.add(bailoutCheckbox_04, gbc_bailoutCheckbox_04);
    gasNameLabel_04 = new JLabel("AIR");
    gasNameLabel_04.setForeground(new Color(0, 0, 128));
    gasNameLabel_04.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_gasNameLabel_04 = new GridBagConstraints();
    gbc_gasNameLabel_04.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_04.insets = new Insets(0, 0, 5, 5);
    gbc_gasNameLabel_04.gridx = 10;
    gbc_gasNameLabel_04.gridy = 5;
    gasMatrixPanel.add(gasNameLabel_04, gbc_gasNameLabel_04);
    borderGasLabel_04 = new JLabel("-");
    borderGasLabel_04.setForeground(Color.BLUE);
    borderGasLabel_04.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_borderGasLabel_04 = new GridBagConstraints();
    gbc_borderGasLabel_04.anchor = GridBagConstraints.WEST;
    gbc_borderGasLabel_04.insets = new Insets(0, 0, 5, 0);
    gbc_borderGasLabel_04.gridx = 11;
    gbc_borderGasLabel_04.gridy = 5;
    gasMatrixPanel.add(borderGasLabel_04, gbc_borderGasLabel_04);
    gasLabel_05 = new JLabel("GAS05");
    GridBagConstraints gbc_gasLabel_05 = new GridBagConstraints();
    gbc_gasLabel_05.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_05.insets = new Insets(0, 0, 5, 5);
    gbc_gasLabel_05.gridx = 1;
    gbc_gasLabel_05.gridy = 6;
    gasMatrixPanel.add(gasLabel_05, gbc_gasLabel_05);
    gasO2Spinner_05 = new JSpinner();
    gasO2Spinner_05.setBounds(new Rectangle(0, 0, 50, 0));
    gasO2Spinner_05.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasO2Spinner_05 = new GridBagConstraints();
    gbc_gasO2Spinner_05.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_05.insets = new Insets(0, 0, 5, 5);
    gbc_gasO2Spinner_05.gridx = 3;
    gbc_gasO2Spinner_05.gridy = 6;
    gasMatrixPanel.add(gasO2Spinner_05, gbc_gasO2Spinner_05);
    gasHESpinner_05 = new JSpinner();
    gasHESpinner_05.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasHESpinner_05 = new GridBagConstraints();
    gbc_gasHESpinner_05.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_05.insets = new Insets(0, 0, 5, 5);
    gbc_gasHESpinner_05.gridx = 5;
    gbc_gasHESpinner_05.gridy = 6;
    gasMatrixPanel.add(gasHESpinner_05, gbc_gasHESpinner_05);
    diluent1Checkbox_05 = new JCheckBox("D1");
    duluent1ButtonGroup.add(diluent1Checkbox_05);
    GridBagConstraints gbc_diluent1Checkbox_05 = new GridBagConstraints();
    gbc_diluent1Checkbox_05.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_05.insets = new Insets(0, 0, 5, 5);
    gbc_diluent1Checkbox_05.gridx = 7;
    gbc_diluent1Checkbox_05.gridy = 6;
    gasMatrixPanel.add(diluent1Checkbox_05, gbc_diluent1Checkbox_05);
    diluent2Checkbox_05 = new JCheckBox("D2");
    diluent2ButtonGroup.add(diluent2Checkbox_05);
    GridBagConstraints gbc_diluent2Checkbox_05 = new GridBagConstraints();
    gbc_diluent2Checkbox_05.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_05.insets = new Insets(0, 0, 5, 5);
    gbc_diluent2Checkbox_05.gridx = 8;
    gbc_diluent2Checkbox_05.gridy = 6;
    gasMatrixPanel.add(diluent2Checkbox_05, gbc_diluent2Checkbox_05);
    bailoutCheckbox_05 = new JCheckBox("B");
    GridBagConstraints gbc_bailoutCheckbox_05 = new GridBagConstraints();
    gbc_bailoutCheckbox_05.insets = new Insets(0, 0, 5, 5);
    gbc_bailoutCheckbox_05.gridx = 9;
    gbc_bailoutCheckbox_05.gridy = 6;
    gasMatrixPanel.add(bailoutCheckbox_05, gbc_bailoutCheckbox_05);
    gasNameLabel_05 = new JLabel("AIR");
    gasNameLabel_05.setForeground(new Color(0, 0, 128));
    gasNameLabel_05.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_gasNameLabel_05 = new GridBagConstraints();
    gbc_gasNameLabel_05.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_05.insets = new Insets(0, 0, 5, 5);
    gbc_gasNameLabel_05.gridx = 10;
    gbc_gasNameLabel_05.gridy = 6;
    gasMatrixPanel.add(gasNameLabel_05, gbc_gasNameLabel_05);
    borderGasLabel_05 = new JLabel("-");
    borderGasLabel_05.setForeground(Color.BLUE);
    borderGasLabel_05.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_borderGasLabel_05 = new GridBagConstraints();
    gbc_borderGasLabel_05.anchor = GridBagConstraints.WEST;
    gbc_borderGasLabel_05.insets = new Insets(0, 0, 5, 0);
    gbc_borderGasLabel_05.gridx = 11;
    gbc_borderGasLabel_05.gridy = 6;
    gasMatrixPanel.add(borderGasLabel_05, gbc_borderGasLabel_05);
    gasLabel_06 = new JLabel("GAS06");
    GridBagConstraints gbc_gasLabel_06 = new GridBagConstraints();
    gbc_gasLabel_06.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_06.insets = new Insets(0, 0, 5, 5);
    gbc_gasLabel_06.gridx = 1;
    gbc_gasLabel_06.gridy = 7;
    gasMatrixPanel.add(gasLabel_06, gbc_gasLabel_06);
    gasO2Spinner_06 = new JSpinner();
    gasO2Spinner_06.setBounds(new Rectangle(0, 0, 50, 0));
    gasO2Spinner_06.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasO2Spinner_06 = new GridBagConstraints();
    gbc_gasO2Spinner_06.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_06.insets = new Insets(0, 0, 5, 5);
    gbc_gasO2Spinner_06.gridx = 3;
    gbc_gasO2Spinner_06.gridy = 7;
    gasMatrixPanel.add(gasO2Spinner_06, gbc_gasO2Spinner_06);
    gasHESpinner_06 = new JSpinner();
    gasHESpinner_06.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasHESpinner_06 = new GridBagConstraints();
    gbc_gasHESpinner_06.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_06.insets = new Insets(0, 0, 5, 5);
    gbc_gasHESpinner_06.gridx = 5;
    gbc_gasHESpinner_06.gridy = 7;
    gasMatrixPanel.add(gasHESpinner_06, gbc_gasHESpinner_06);
    diluent1Checkbox_06 = new JCheckBox("D1");
    duluent1ButtonGroup.add(diluent1Checkbox_06);
    GridBagConstraints gbc_diluent1Checkbox_06 = new GridBagConstraints();
    gbc_diluent1Checkbox_06.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_06.insets = new Insets(0, 0, 5, 5);
    gbc_diluent1Checkbox_06.gridx = 7;
    gbc_diluent1Checkbox_06.gridy = 7;
    gasMatrixPanel.add(diluent1Checkbox_06, gbc_diluent1Checkbox_06);
    diluent2Checkbox_06 = new JCheckBox("D2");
    diluent2ButtonGroup.add(diluent2Checkbox_06);
    GridBagConstraints gbc_diluent2Checkbox_06 = new GridBagConstraints();
    gbc_diluent2Checkbox_06.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_06.insets = new Insets(0, 0, 5, 5);
    gbc_diluent2Checkbox_06.gridx = 8;
    gbc_diluent2Checkbox_06.gridy = 7;
    gasMatrixPanel.add(diluent2Checkbox_06, gbc_diluent2Checkbox_06);
    bailoutCheckbox_06 = new JCheckBox("B");
    GridBagConstraints gbc_bailoutCheckbox_06 = new GridBagConstraints();
    gbc_bailoutCheckbox_06.insets = new Insets(0, 0, 5, 5);
    gbc_bailoutCheckbox_06.gridx = 9;
    gbc_bailoutCheckbox_06.gridy = 7;
    gasMatrixPanel.add(bailoutCheckbox_06, gbc_bailoutCheckbox_06);
    gasNameLabel_06 = new JLabel("AIR");
    gasNameLabel_06.setForeground(new Color(0, 0, 128));
    gasNameLabel_06.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_gasNameLabel_06 = new GridBagConstraints();
    gbc_gasNameLabel_06.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_06.insets = new Insets(0, 0, 5, 5);
    gbc_gasNameLabel_06.gridx = 10;
    gbc_gasNameLabel_06.gridy = 7;
    gasMatrixPanel.add(gasNameLabel_06, gbc_gasNameLabel_06);
    borderGasLabel_06 = new JLabel("-");
    borderGasLabel_06.setForeground(Color.BLUE);
    borderGasLabel_06.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_borderGasLabel_06 = new GridBagConstraints();
    gbc_borderGasLabel_06.anchor = GridBagConstraints.WEST;
    gbc_borderGasLabel_06.insets = new Insets(0, 0, 5, 0);
    gbc_borderGasLabel_06.gridx = 11;
    gbc_borderGasLabel_06.gridy = 7;
    gasMatrixPanel.add(borderGasLabel_06, gbc_borderGasLabel_06);
    gasLabel_07 = new JLabel("GAS07");
    GridBagConstraints gbc_gasLabel_07 = new GridBagConstraints();
    gbc_gasLabel_07.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_07.insets = new Insets(0, 0, 0, 5);
    gbc_gasLabel_07.gridx = 1;
    gbc_gasLabel_07.gridy = 8;
    gasMatrixPanel.add(gasLabel_07, gbc_gasLabel_07);
    gasO2Spinner_07 = new JSpinner();
    gasO2Spinner_07.setBounds(new Rectangle(0, 0, 50, 0));
    gasO2Spinner_07.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasO2Spinner_07 = new GridBagConstraints();
    gbc_gasO2Spinner_07.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_07.insets = new Insets(0, 0, 0, 5);
    gbc_gasO2Spinner_07.gridx = 3;
    gbc_gasO2Spinner_07.gridy = 8;
    gasMatrixPanel.add(gasO2Spinner_07, gbc_gasO2Spinner_07);
    gasHESpinner_07 = new JSpinner();
    gasHESpinner_07.setAlignmentX(1.0f);
    GridBagConstraints gbc_gasHESpinner_07 = new GridBagConstraints();
    gbc_gasHESpinner_07.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_07.insets = new Insets(0, 0, 0, 5);
    gbc_gasHESpinner_07.gridx = 5;
    gbc_gasHESpinner_07.gridy = 8;
    gasMatrixPanel.add(gasHESpinner_07, gbc_gasHESpinner_07);
    diluent1Checkbox_07 = new JCheckBox("D1");
    duluent1ButtonGroup.add(diluent1Checkbox_07);
    GridBagConstraints gbc_diluent1Checkbox_07 = new GridBagConstraints();
    gbc_diluent1Checkbox_07.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_07.insets = new Insets(0, 0, 0, 5);
    gbc_diluent1Checkbox_07.gridx = 7;
    gbc_diluent1Checkbox_07.gridy = 8;
    gasMatrixPanel.add(diluent1Checkbox_07, gbc_diluent1Checkbox_07);
    diluent2Checkbox_07 = new JCheckBox("D2");
    diluent2ButtonGroup.add(diluent2Checkbox_07);
    GridBagConstraints gbc_diluent2Checkbox_07 = new GridBagConstraints();
    gbc_diluent2Checkbox_07.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_07.insets = new Insets(0, 0, 0, 5);
    gbc_diluent2Checkbox_07.gridx = 8;
    gbc_diluent2Checkbox_07.gridy = 8;
    gasMatrixPanel.add(diluent2Checkbox_07, gbc_diluent2Checkbox_07);
    bailoutCheckbox_07 = new JCheckBox("B");
    GridBagConstraints gbc_bailoutCheckbox_07 = new GridBagConstraints();
    gbc_bailoutCheckbox_07.insets = new Insets(0, 0, 0, 5);
    gbc_bailoutCheckbox_07.gridx = 9;
    gbc_bailoutCheckbox_07.gridy = 8;
    gasMatrixPanel.add(bailoutCheckbox_07, gbc_bailoutCheckbox_07);
    gasNameLabel_07 = new JLabel("AIR");
    gasNameLabel_07.setForeground(new Color(0, 0, 128));
    gasNameLabel_07.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_gasNameLabel_07 = new GridBagConstraints();
    gbc_gasNameLabel_07.insets = new Insets(0, 0, 0, 5);
    gbc_gasNameLabel_07.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_07.gridx = 10;
    gbc_gasNameLabel_07.gridy = 8;
    gasMatrixPanel.add(gasNameLabel_07, gbc_gasNameLabel_07);
    borderGasLabel_07 = new JLabel("-");
    borderGasLabel_07.setForeground(Color.BLUE);
    borderGasLabel_07.setFont(new Font("Tahoma", Font.PLAIN, 12));
    GridBagConstraints gbc_borderGasLabel_07 = new GridBagConstraints();
    gbc_borderGasLabel_07.anchor = GridBagConstraints.WEST;
    gbc_borderGasLabel_07.gridx = 11;
    gbc_borderGasLabel_07.gridy = 8;
    gasMatrixPanel.add(borderGasLabel_07, gbc_borderGasLabel_07);
    customPresetComboBox = new JComboBox();
    customPresetComboBox.setActionCommand("preset_changed");
    customPresetComboBox.setEnabled(false);
    customPresetComboBox.setBounds(573, 421, 210, 20);
    add(customPresetComboBox);
    writeGasPresetButton = new JButton("WRITEPRESELECT");
    writeGasPresetButton.setForeground(Color.RED);
    writeGasPresetButton.setBackground(new Color(255, 192, 203));
    writeGasPresetButton.setActionCommand("write_gas_preset");
    writeGasPresetButton.setBounds(573, 448, 210, 33);
    add(writeGasPresetButton);
    licenseStatusLabel = new JLabel("LICENSE");
    licenseStatusLabel.setBounds(10, 385, 553, 14);
    add(licenseStatusLabel);
    salnityCheckBox = new JCheckBox("SALNITY");
    salnityCheckBox.setBounds(346, 386, 217, 23);
    salnityCheckBox.setActionCommand("check_salnity");
    add(salnityCheckBox);
    ppoMaxComboBox = new JComboBox();
    ppoMaxComboBox.setActionCommand("set_ppomax");
    ppoMaxComboBox.setModel(new DefaultComboBoxModel(new String[] { "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6" }));
    ppoMaxComboBox.setSelectedIndex(6);
    ppoMaxComboBox.setBounds(573, 390, 92, 20);
    add(ppoMaxComboBox);
    pressureUnitLabel = new JLabel("BAR");
    pressureUnitLabel.setBounds(675, 390, 46, 14);
    add(pressureUnitLabel);
    buttonComputeGases = new JButton("COMPUTE");
    buttonComputeGases.setRolloverIcon(new ImageIcon(spx42GaslistEditPanel.class.getResource("/de/dmarcini/submatix/pclogger/res/calcColor.png")));
    buttonComputeGases.setIcon(new ImageIcon(spx42GaslistEditPanel.class.getResource("/de/dmarcini/submatix/pclogger/res/calcBw.png")));
    buttonComputeGases.setIconTextGap(15);
    buttonComputeGases.setHorizontalAlignment(SwingConstants.LEFT);
    buttonComputeGases.setForeground(Color.BLUE);
    buttonComputeGases.setBackground(new Color(255, 255, 240));
    buttonComputeGases.setActionCommand("compute_gas");
    buttonComputeGases.setBounds(429, 421, 133, 60);
    add(buttonComputeGases);
    isPanelInitiated = true;
  }

  @Override
  public void itemStateChanged(ItemEvent ev)
  {
    if (ignoreAction) return;
    // ////////////////////////////////////////////////////////////////////////
    // Checkbox Event?
    if (ev.getSource() instanceof JCheckBox)
    {
      JCheckBox cb = (JCheckBox) ev.getItemSelectable();
      String cmd = cb.getActionCommand();
      // //////////////////////////////////////////////////////////////////////
      // Salzwasser?
      if (cmd.equals("check_salnity"))
      {
        lg.debug("salnity <" + cb.isSelected() + ">");
        salnity = cb.isSelected();
        setAllDescriptionsForGas();
      }
      // //////////////////////////////////////////////////////////////////////
      // Bailout checkbox für ein Gas?
      else if (cmd.startsWith("bailout:"))
      {
        String[] fields = fieldPatternDp.split(cmd);
        try
        {
          int idx = Integer.parseInt(fields[1]);
          lg.debug(String.format("Bailout %s changed.", cmd));
          currGasList.setBailout(idx, cb.isSelected());
        }
        catch (NumberFormatException ex)
        {
          lg.error("Exception while recive bailout checkbox event: " + ex.getLocalizedMessage());
        }
      }
      // //////////////////////////////////////////////////////////////////////
      // Diluent 1 für ein Gas setzen?
      else if (cmd.startsWith("diluent1:"))
      {
        String[] fields = fieldPatternDp.split(cmd);
        try
        {
          int idx = Integer.parseInt(fields[1]);
          lg.debug(String.format("Diluent 1  to %d changed.", idx));
          if (cb.isSelected())
          {
            currGasList.setDiluent1(idx);
          }
        }
        catch (NumberFormatException ex)
        {
          lg.error("Exception while recive diluent1 checkbox event: " + ex.getLocalizedMessage());
        }
      }
      // //////////////////////////////////////////////////////////////////////
      // Diluent 2 für ein Gas setzen?
      else if (cmd.startsWith("diluent2:"))
      {
        String[] fields = fieldPatternDp.split(cmd);
        try
        {
          int idx = Integer.parseInt(fields[1]);
          lg.debug(String.format("Diluent 2  to %d changed.", idx));
          if (cb.isSelected())
          {
            currGasList.setDiluent2(idx);
          }
        }
        catch (NumberFormatException ex)
        {
          lg.error("Exception while recive diluent1 checkbox event: " + ex.getLocalizedMessage());
        }
      }
      else
      {
        lg.warn("unknown checkbox item changed: <" + cb.getActionCommand() + "> <" + cb.isSelected() + ">");
      }
    }
    else if (ev.getSource() instanceof JComboBox)
    {
      if (ev.getStateChange() != ItemEvent.SELECTED) return;
      JComboBox cb = (JComboBox) ev.getSource();
      String cmd = cb.getActionCommand();
      // //////////////////////////////////////////////////////////////////////
      // die Preset-Combobox
      if (cmd.equals("preset_changed"))
      {
        if (cb.getSelectedIndex() == -1) return;
        int index = customPresetComboBox.getSelectedIndex();
        String presetName = ((GasPresetComboBoxModel) customPresetComboBox.getModel()).getNameAt(index);
        int dbId = ((GasPresetComboBoxModel) customPresetComboBox.getModel()).getDatabaseIdAt(index);
        lg.debug("preset combobox changed to index <" + index + ">");
        lg.debug("entry has name <" + presetName + "> and dbId <" + dbId + ">");
        prepareCurentGasFromDb(dbId);
        return;
      }
    }
    else
    {
      lg.warn("unknown item changed!");
    }
  }

  /**
   * Lade aktuelle Gasliste von der Datenbank
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 23.09.2012
   * @param dbId
   */
  private void prepareCurentGasFromDb(int dbId)
  {
    SPX42GasList tempGasList = databaseUtil.getPresetForSetId(dbId);
    if (!tempGasList.isInitialized())
    {
      lg.error("gaslist is not initialized! ABORT!");
      return;
    }
    //
    // Checke, ob die Gasliste zu der Lizenz passt
    //
    if (licenseState == ProjectConst.SPX_LICENSE_NOT_SET || licenseState == ProjectConst.SPX_LICENSE_NITROX)
    {
      // alle gase auf he==0 und o2 >= 21 checken
      for (int idx = 0; idx < tempGasList.getGasCount(); idx++)
      {
        if (tempGasList.getHEFromGas(idx) > 0)
        {
          showNoLicenseBox(stringsBundle.getString("spx42GaslistEditPanel.showNoLicenseBox.nottx"));
          customPresetComboBox.setSelectedIndex(0);
          return;
        }
        if (tempGasList.getO2FromGas(idx) < 21)
        {
          showNoLicenseBox(stringsBundle.getString("spx42GaslistEditPanel.showNoLicenseBox.nohypoox"));
          customPresetComboBox.setSelectedIndex(0);
          return;
        }
      }
    }
    else if (licenseState == ProjectConst.SPX_LICENSE_NORMOXICTX)
    {
      // alle gase auf o2 >= 18 checken
      for (int idx = 0; idx < tempGasList.getGasCount(); idx++)
      {
        if (tempGasList.getO2FromGas(idx) < 18)
        {
          showNoLicenseBox(stringsBundle.getString("spx42GaslistEditPanel.showNoLicenseBox.nohypoox"));
          customPresetComboBox.setSelectedIndex(0);
          return;
        }
      }
    }
    //
    // Gase initialisieren
    //
    currGasList = tempGasList;
    initGasesFromCurrent();
  }

  /**
   * Panel GUI initialisieren
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 02.09.2012
   */
  public void prepareGasslistPanel()
  {
    initPanel();
    initGasObjectMaps();
    if (!isOnline)
    {
      licenseState = ProjectConst.SPX_LICENSE_FULLTX;
    }
    setLanguageStrings(stringsBundle);
    setLicenseLabel(stringsBundle);
    setAllGasPanels();
    fillPresetComboBox();
    setGlobalChangeListener(mainCommGUI);
  }

  /**
   * Daten des Panels freigeben
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 02.09.2012
   */
  public void releasePanel()
  {
    isPanelInitiated = false;
    this.removeAll();
    o2SpinnerMap.clear();
    heSpinnerMap.clear();
    gasLblMap.clear();
    gasLblMap2.clear();
    bailoutMap.clear();
    diluent1Map.clear();
    diluent2Map.clear();
  }

  /**
   * Alle Beschreibungen neu setzen
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 03.09.2012
   */
  private void setAllDescriptionsForGas()
  {
    int i, o2, he;
    if (!isPanelInitiated) return;
    for (i = 0; i < 8; i++)
    {
      o2 = (Integer) o2SpinnerMap.get(i).getValue();
      he = (Integer) heSpinnerMap.get(i).getValue();
      setDescriptionForGas(i, o2, he);
    }
  }

  /**
   * Alles im Panel vorbereiten
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 22.04.2012
   * @param en
   */
  private void setAllGasPanels()
  {
    if (!isPanelInitiated) return;
    lg.debug("setAllGasPanels...");
    for (Component cp : gasMatrixPanel.getComponents())
    {
      // License State 0=Nitrox,1=Normoxic Trimix,2=Full Trimix
      // isses ein Spinner?
      if (cp instanceof JSpinner)
      {
        JSpinner currSpinner = (JSpinner) cp;
        // welcher Lizenzstatus
        // ////////////////////////////////////////////////////////////////////
        // Ist es NITROX?
        if (licenseState <= ProjectConst.SPX_LICENSE_NITROX)
        {
          lg.debug("setAllGasPanels...NITROX...");
          // issen einer von den Helium-Teilen
          for (Integer idx : heSpinnerMap.keySet())
          {
            JSpinner sp = heSpinnerMap.get(idx);
            if (currSpinner.equals(sp))
            {
              // ja, ein Helium-Teil, NITROX enabled, also sperre das Ding
              sp.setValue(0);
              sp.setEnabled(false);
            }
          }
          // ein Sauerstoffteil?
          for (Integer idx : o2SpinnerMap.keySet())
          {
            JSpinner sp = o2SpinnerMap.get(idx);
            if (currSpinner.equals(sp))
            {
              // ja, ein Sauerstoff-Teil, NITROX enabled, setze Range von 18 bis 100%
              sp.setEnabled(true);
              sp.setModel(new SpinnerNumberModel(21, 18, 100, 1));
            }
          }
        }
        // ////////////////////////////////////////////////////////////////////
        // ist es Normoxic Trimix?
        if (licenseState == ProjectConst.SPX_LICENSE_NORMOXICTX)
        {
          lg.debug("setAllGasPanels...NORMOXICTX...");
          // issen einer von den Helium-Teilen / Normoxic Trimix enabled
          for (Integer idx : heSpinnerMap.keySet())
          {
            JSpinner sp = heSpinnerMap.get(idx);
            if (currSpinner.equals(sp))
            {
              // ja, ein Helium-Teil, max 79 Prozent Helium
              sp.setEnabled(true);
              sp.setModel(new SpinnerNumberModel(0, 0, 79, 1));
            }
          }
          // ein Sauerstoffteil?
          for (Integer idx : o2SpinnerMap.keySet())
          {
            JSpinner sp = o2SpinnerMap.get(idx);
            if (currSpinner.equals(sp))
            {
              // ja, ein O2-Teil, NORMOXIC Trimix enabled, Bereich von 18 bis 100%
              sp.setEnabled(true);
              sp.setModel(new SpinnerNumberModel(21, 18, 100, 1));
            }
          }
        }
        // ////////////////////////////////////////////////////////////////////
        // ist es FULL Trimix
        else if (licenseState == ProjectConst.SPX_LICENSE_FULLTX)
        {
          lg.debug("setAllGasPanels...FULLTX...");
          // Full Trimix
          // issen einer von den Helium-Teilen
          for (Integer idx : heSpinnerMap.keySet())
          {
            JSpinner sp = heSpinnerMap.get(idx);
            if (currSpinner.equals(sp))
            {
              // ja, ein Helium-Teil, full Trimix
              currSpinner.setEnabled(true);
              currSpinner.setModel(new SpinnerNumberModel(0, 0, 99, 1));
            }
          }
          // ein Sauerstoffteil?
          for (Integer idx : o2SpinnerMap.keySet())
          {
            JSpinner sp = o2SpinnerMap.get(idx);
            if (currSpinner.equals(sp))
            {
              // ja, ein O2-Teil, Full Trimmix enabled
              currSpinner.setEnabled(true);
              currSpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));
            }
          }
        }
      }
      else
      {
        cp.setEnabled(true);
      }
    }
    gasMatrixPanel.setEnabled(true);
    gasWriteToSPXButton.setEnabled(isOnline);
    gasReadFromSPXButton.setEnabled(isOnline);
    customPresetComboBox.setEnabled(true);
    writeGasPresetButton.setEnabled(true);
    //
    // farbig hervorheben
    //
    if (isOnline)
    {
      gasMatrixPanel.setBorder(new LineBorder(Color.GREEN, 3, true));
    }
    else
    {
      gasMatrixPanel.setBorder(new LineBorder(Color.BLUE, 1, true));
    }
  }

  /**
   * Setze die Bezeichnung, die MOD, EAD und Farbe für das Gas ins Label
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 03.09.2012
   * @param i
   * @param o2
   * @param he
   */
  public void setDescriptionForGas(int i, int o2, int he)
  {
    double mod, ead;
    int n2 = 100 - (o2 + he);
    if (!isPanelInitiated) return;
    gasLblMap.get(i).setText(GasComputeUnit.getNameForGas(o2, he));
    setGasColor(i, o2);
    if (unitsString.equals("metric"))
    {
      // MOD und EAD berechnen
      mod = GasComputeUnit.getMODForGasMetric(o2, ppOMax, salnity);
      ead = GasComputeUnit.getEADForGasMetric(n2, mod, salnity);
      // MOD und EAD in String umformen und in das richtige Label schreiben
      gasLblMap2.get(i).setText(String.format(stringsBundle.getString("spx42GaslistEditPanel.mod-ead-label.metric"), Math.round(mod), Math.round(ead)));
    }
    else
    {
      mod = GasComputeUnit.getMODForGasImperial(o2, ppOMax, salnity);
      ead = GasComputeUnit.getEADForGasImperial(n2, mod, salnity);
      gasLblMap2.get(i).setText(String.format(stringsBundle.getString("spx42GaslistEditPanel.mod-ead-label.imperial"), Math.round(mod), Math.round(ead)));
    }
  }

  /**
   * Färbe die Texte für die Gasse noch ordentlich ein
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 30.07.2012
   * @param gasNr
   * @param o2
   */
  private void setGasColor(int gasNr, int o2)
  {
    if (o2 < 14)
    {
      (gasLblMap.get(gasNr)).setForeground(ProjectConst.GASNAMECOLOR_DANGEROUS);
      ((NumberEditor) (o2SpinnerMap.get(gasNr).getEditor())).getTextField().setForeground(ProjectConst.GASNAMECOLOR_DANGEROUS);
    }
    else if (o2 < 21)
    {
      (gasLblMap.get(gasNr)).setForeground(ProjectConst.GASNAMECOLOR_NONORMOXIC);
      ((NumberEditor) (o2SpinnerMap.get(gasNr).getEditor())).getTextField().setForeground(ProjectConst.GASNAMECOLOR_NONORMOXIC);
    }
    else
    {
      (gasLblMap.get(gasNr)).setForeground(ProjectConst.GASNAMECOLOR_NORMAL);
      ((NumberEditor) (o2SpinnerMap.get(gasNr).getEditor())).getTextField().setForeground(ProjectConst.GASNAMECOLOR_NORMAL);
    }
  }

  /**
   * Alle Change Listener für Spinner setzen
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 18.04.2012
   * @param mainCommGUI
   */
  public void setGlobalChangeListener(MainCommGUI mainCommGUI)
  {
    this.mainCommGUI = mainCommGUI;
    if (!isPanelInitiated) return;
    for (Integer idx : o2SpinnerMap.keySet())
    {
      JSpinner sp = o2SpinnerMap.get(idx);
      sp.addChangeListener(this);
    }
    //
    for (Integer idx : heSpinnerMap.keySet())
    {
      JSpinner sp = heSpinnerMap.get(idx);
      sp.addChangeListener(this);
    }
    //
    for (Integer idx : bailoutMap.keySet())
    {
      JCheckBox cb = bailoutMap.get(idx);
      cb.addItemListener(this);
      cb.setActionCommand(String.format("bailout:%d", idx));
    }
    //
    for (Integer idx : diluent1Map.keySet())
    {
      JCheckBox cb = diluent1Map.get(idx);
      cb.addItemListener(this);
      cb.setActionCommand(String.format("diluent1:%d", idx));
    }
    //
    for (Integer idx : diluent2Map.keySet())
    {
      JCheckBox cb = diluent2Map.get(idx);
      cb.addItemListener(this);
      cb.setActionCommand(String.format("diluent2:%d", idx));
    }
    //
    gasReadFromSPXButton.addActionListener(this);
    gasReadFromSPXButton.addActionListener(mainCommGUI);
    gasReadFromSPXButton.addMouseMotionListener(mainCommGUI);
    //
    gasWriteToSPXButton.addActionListener(mainCommGUI);
    gasWriteToSPXButton.addMouseMotionListener(mainCommGUI);
    //
    salnityCheckBox.addMouseMotionListener(mainCommGUI);
    salnityCheckBox.addItemListener(this);
    //
    ppoMaxComboBox.addActionListener(this);
    ppoMaxComboBox.addMouseMotionListener(mainCommGUI);
    //
    customPresetComboBox.addItemListener(this);
    customPresetComboBox.addMouseMotionListener(mainCommGUI);
    //
    writeGasPresetButton.addActionListener(this);
    writeGasPresetButton.addMouseMotionListener(mainCommGUI);
    //
    buttonComputeGases.addActionListener(this);
    buttonComputeGases.addMouseMotionListener(mainCommGUI);
  }

  /**
   * Lokale Sprachenstrings einbauen
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 07.10.2012
   * @param stringsBundle
   * @return erfolgreich oder nicht
   */
  public int setLanguageStrings(ResourceBundle stringsBundle)
  {
    this.stringsBundle = stringsBundle;
    if (!isPanelInitiated) return (-1);
    if (stringsBundle == null) return (-1);
    // so, ignoriere mal alles....
    try
    {
      // //////////////////////////////////////////////////////////////////////
      // Tabbes Pane gas
      String gasLabelStr = stringsBundle.getString("spx42GaslistEditPanel.gasPanel.gasLabel");
      gasLabel_00.setText(String.format("%s %02d", gasLabelStr, 1));
      gasLabel_01.setText(String.format("%s %02d", gasLabelStr, 2));
      gasLabel_02.setText(String.format("%s %02d", gasLabelStr, 3));
      gasLabel_03.setText(String.format("%s %02d", gasLabelStr, 4));
      gasLabel_04.setText(String.format("%s %02d", gasLabelStr, 5));
      gasLabel_05.setText(String.format("%s %02d", gasLabelStr, 6));
      gasLabel_06.setText(String.format("%s %02d", gasLabelStr, 7));
      gasLabel_07.setText(String.format("%s %02d", gasLabelStr, 8));
      gasLabelStr = stringsBundle.getString("spx42GaslistEditPanel.gasPanel.diluentLabel") + "1";
      diluent1Checkbox_00.setText(gasLabelStr);
      diluent1Checkbox_01.setText(gasLabelStr);
      diluent1Checkbox_02.setText(gasLabelStr);
      diluent1Checkbox_03.setText(gasLabelStr);
      diluent1Checkbox_04.setText(gasLabelStr);
      diluent1Checkbox_05.setText(gasLabelStr);
      diluent1Checkbox_06.setText(gasLabelStr);
      diluent1Checkbox_07.setText(gasLabelStr);
      gasLabelStr = stringsBundle.getString("spx42GaslistEditPanel.gasPanel.diluentLabel") + "2";
      diluent2Checkbox_00.setText(gasLabelStr);
      diluent2Checkbox_01.setText(gasLabelStr);
      diluent2Checkbox_02.setText(gasLabelStr);
      diluent2Checkbox_03.setText(gasLabelStr);
      diluent2Checkbox_04.setText(gasLabelStr);
      diluent2Checkbox_05.setText(gasLabelStr);
      diluent2Checkbox_06.setText(gasLabelStr);
      diluent2Checkbox_07.setText(gasLabelStr);
      gasLabelStr = stringsBundle.getString("spx42GaslistEditPanel.gasPanel.bailoutLabel");
      bailoutCheckbox_00.setText(gasLabelStr);
      bailoutCheckbox_01.setText(gasLabelStr);
      bailoutCheckbox_02.setText(gasLabelStr);
      bailoutCheckbox_03.setText(gasLabelStr);
      bailoutCheckbox_04.setText(gasLabelStr);
      bailoutCheckbox_05.setText(gasLabelStr);
      bailoutCheckbox_06.setText(gasLabelStr);
      bailoutCheckbox_07.setText(gasLabelStr);
      gasReadFromSPXButton.setText(stringsBundle.getString("spx42GaslistEditPanel.gasPanel.gasReadFromSPXButton.text"));
      gasReadFromSPXButton.setToolTipText(stringsBundle.getString("spx42GaslistEditPanel.gasPanel.gasReadFromSPXButton.tooltiptext"));
      gasWriteToSPXButton.setText(stringsBundle.getString("spx42GaslistEditPanel.gasPanel.gasWriteToSPXButton.text"));
      gasWriteToSPXButton.setToolTipText(stringsBundle.getString("spx42GaslistEditPanel.gasPanel.gasWriteToSPXButton.tooltiptext"));
      writeGasPresetButton.setText(stringsBundle.getString("spx42GaslistEditPanel.gasPanel.writeGasPresetButton.text"));
      writeGasPresetButton.setToolTipText(stringsBundle.getString("spx42GaslistEditPanel.gasPanel.writeGasPresetButton.tooltiptext"));
      customPresetComboBox.setToolTipText(stringsBundle.getString("spx42GaslistEditPanel.gasPanel.customPresetComboBox.tooltiptext"));
      salnityCheckBox.setText(stringsBundle.getString("spx42GaslistEditPanel.salnityCheckBox.text"));
      salnityCheckBox.setToolTipText(stringsBundle.getString("spx42GaslistEditPanel.salnityCheckBox.tooltiptext"));
      buttonComputeGases.setText(stringsBundle.getString("spx42GaslistEditPanel.buttonComputeGases.text"));
      buttonComputeGases.setToolTipText(stringsBundle.getString("spx42GaslistEditPanel.buttonComputeGases.tooltiptext"));
      String[] pressureStrings = new String[7];
      // Voreinstellung für Einheiten auf dieser Seite
      if (progConfig.getUnitsProperty() == ProjectConst.UNITS_DEFAULT)
      {
        // Wenn in der Config default vorgesehen ist
        unitsString = stringsBundle.getString("MainCommGUI.unitsDefault");
      }
      else
      {
        if (progConfig.getUnitsProperty() == ProjectConst.UNITS_IMPERIAL)
        {
          // ist imperial vorgesehen
          unitsString = "imperial";
        }
      }
      //
      // jetz hab ich eine gewünschte Einheiteneinstellung für die Berechnungen
      //
      if (unitsString.equals("metric"))
      {
        pressureStrings[0] = stringsBundle.getString("spx42GaslistEditPanel.pressures.metric.0");
        pressureStrings[1] = stringsBundle.getString("spx42GaslistEditPanel.pressures.metric.1");
        pressureStrings[2] = stringsBundle.getString("spx42GaslistEditPanel.pressures.metric.2");
        pressureStrings[3] = stringsBundle.getString("spx42GaslistEditPanel.pressures.metric.3");
        pressureStrings[4] = stringsBundle.getString("spx42GaslistEditPanel.pressures.metric.4");
        pressureStrings[5] = stringsBundle.getString("spx42GaslistEditPanel.pressures.metric.5");
        pressureStrings[6] = stringsBundle.getString("spx42GaslistEditPanel.pressures.metric.6");
        ppoMaxComboBox.setModel(new DefaultComboBoxModel(pressureStrings));
        ppoMaxComboBox.setSelectedIndex(pressureStrings.length - 1);
        pressureUnitLabel.setText(stringsBundle.getString("spx42GaslistEditPanel.pressureUnitLabel.metric"));
      }
      else
      {
        pressureStrings[0] = stringsBundle.getString("spx42GaslistEditPanel.pressures.imperial.0");
        pressureStrings[1] = stringsBundle.getString("spx42GaslistEditPanel.pressures.imperial.1");
        pressureStrings[2] = stringsBundle.getString("spx42GaslistEditPanel.pressures.imperial.2");
        pressureStrings[3] = stringsBundle.getString("spx42GaslistEditPanel.pressures.imperial.3");
        pressureStrings[4] = stringsBundle.getString("spx42GaslistEditPanel.pressures.imperial.4");
        pressureStrings[5] = stringsBundle.getString("spx42GaslistEditPanel.pressures.imperial.5");
        pressureStrings[6] = stringsBundle.getString("spx42GaslistEditPanel.pressures.imperial.6");
        ppoMaxComboBox.setModel(new DefaultComboBoxModel(pressureStrings));
        ppoMaxComboBox.setSelectedIndex(pressureStrings.length - 1);
        pressureUnitLabel.setText(stringsBundle.getString("spx42GaslistEditPanel.pressureUnitLabel.imperial"));
      }
      ppoMaxComboBox.setToolTipText(stringsBundle.getString("spx42GaslistEditPanel.ppoMaxComboBox.tooltiptext"));
      setLicenseLabel(stringsBundle);
    }
    catch (NullPointerException ex)
    {
      System.out.println("ERROR set language strings <" + ex.getMessage() + "> ABORT!");
      return (-1);
    }
    catch (MissingResourceException ex)
    {
      System.out.println("ERROR set language strings - the given key can be found <" + ex.getMessage() + "> ABORT!");
      return (-1);
    }
    catch (ClassCastException ex)
    {
      System.out.println("ERROR set language strings <" + ex.getMessage() + "> ABORT!");
      return (-1);
    }
    return (1);
  }

  /**
   * Entsprechend der Lizenzlage Anzeigen, welcher Lizenzstatus korrekt ist.
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 21.04.2012
   * @param stringsBundle
   */
  public void setLicenseLabel(ResourceBundle stringsBundle)
  {
    if (!isPanelInitiated) return;
    String licString;
    switch (licenseState)
    {
      case ProjectConst.SPX_LICENSE_NOT_SET:
        // nicht konfiguriert
        licString = " ";
      case ProjectConst.SPX_LICENSE_NITROX:
        // Nitrox
        licString = stringsBundle.getString("spx42GaslistEditPanel.gasPanel.licenseLabel.nitrox.text");
        break;
      case ProjectConst.SPX_LICENSE_NORMOXICTX:
        licString = stringsBundle.getString("spx42GaslistEditPanel.gasPanel.licenseLabel.normoxic.text");
        break;
      default:
        licString = stringsBundle.getString("spx42GaslistEditPanel.gasPanel.licenseLabel.fulltrimix.text");
    }
    //
    switch (customConfig)
    {
      case -1:
      case 0:
        // nicht konfiguriert/nicht erlaubt
        licenseStatusLabel.setText(licString);
        break;
      case 1:
        // erlaubt
        licenseStatusLabel.setText(licString + " - " + stringsBundle.getString("spx42GaslistEditPanel.gasPanel.licenseLabel.customconfigEnabled.text"));
      default:
        licenseStatusLabel.setText(licString);
    }
  }

  /**
   * Lizenzstatus setzen
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 22.04.2012
   * @param lic
   * @param cust
   */
  public void setLicenseState(int lic, int cust)
  {
    licenseState = lic;
    customConfig = cust;
  }

  /**
   * Ist das Pannel im Online-Mode?
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 07.10.2012
   * @param online
   * @return Online Modus
   */
  public boolean setPanelOnlineMode(boolean online)
  {
    this.isOnline = online;
    lg.debug("setPanelOnlineMode: " + online);
    if (!online)
    {
      // Kein SPX da, muss ich also annehmen, ich hab alle Lizenzen
      licenseState = ProjectConst.SPX_LICENSE_FULLTX;
    }
    setLicenseLabel(stringsBundle);
    // Panels setzen
    setAllGasPanels();
    return (this.isOnline);
  }

  /**
   * Dialogbox anzeigen, die mitteilt, daß die Lizenz nicht ausreicht
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 23.09.2012
   * @param string
   */
  private void showNoLicenseBox(String msg)
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon(MainCommGUI.class.getResource("/de/dmarcini/submatix/pclogger/res/Terminate.png"));
      JOptionPane.showMessageDialog(this, msg, stringsBundle.getString("spx42GaslistEditPanel.showNoLicenseBox.headline"), JOptionPane.INFORMATION_MESSAGE,
              icon);
    }
    catch (NullPointerException ex)
    {
      lg.error("ERROR showErrorBox <" + ex.getMessage() + "> ABORT!");
      return;
    }
    catch (MissingResourceException ex)
    {
      lg.error("ERROR showErrorBox <" + ex.getMessage() + "> ABORT!");
      return;
    }
    catch (ClassCastException ex)
    {
      lg.error("ERROR showErrorBox <" + ex.getMessage() + "> ABORT!");
      return;
    }
  }

  /**
   * Zeige eine Dialogbox zum eingeben eines Namens für das Preset
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   *         Stand: 23.09.2012
   * @param dbId
   *          Id des Presets oder -1
   * @param oldName
   *          der Name des Presets
   */
  private void showPresetSaveEditForm(String oldName, int dbId)
  {
    GasPresetNameDialog edDial = null;
    //
    // vorbereitung...
    //
    if (currGasList == null) return;
    if (!currGasList.isInitialized()) return;
    //
    edDial = new GasPresetNameDialog(stringsBundle);
    edDial.setName(oldName);
    if (edDial.showModal())
    {
      if (dbId == -1)
      {
        lg.info("save NEW Preset in database...");
        // das wird ein neues Preset
        databaseUtil.saveNewPresetData(edDial.getName(), currGasList);
      }
      else
      {
        lg.info("save Preset in database...");
        // Wurde der Name auch verändert?
        if (oldName.equals(edDial.getName()))
        {
          // nein, Name bleibt gleich
          databaseUtil.updatePresetData(dbId, currGasList);
        }
        else
        {
          // Name auch verändern
          databaseUtil.updatePresetData(dbId, edDial.getName(), currGasList);
        }
      }
      edDial.dispose();
    }
    else
    {
      edDial.dispose();
    }
  }

  @Override
  public void stateChanged(ChangeEvent ev)
  {
    JSpinner currSpinner;
    int currValue;
    if (ignoreAction) return;
    // //////////////////////////////////////////////////////////////////////
    // war es ein spinner?
    if (ev.getSource() instanceof JSpinner)
    {
      currSpinner = (JSpinner) ev.getSource();
      // //////////////////////////////////////////////////////////////////////
      // Nix gefunden, also versuch mal die Listen durch
      if (heSpinnerMap == null) return;
      if (o2SpinnerMap == null) return;
      for (int gasNr = 0; gasNr < currGasList.getGasCount(); gasNr++)
      {
        if (currSpinner.equals(o2SpinnerMap.get(gasNr)))
        {
          // O2 Spinner betätigt
          // Gas <gasNr> Sauerstoffanteil ändern
          currValue = (Integer) currSpinner.getValue();
          changeO2FromGas(gasNr, currValue);
          return;
        }
        else if (currSpinner.equals(heSpinnerMap.get(gasNr)))
        {
          // Heliumspinner betätigt
          // Gas <gasNr> Heliumanteil ändern
          currValue = (Integer) currSpinner.getValue();
          changeHEFromGas(gasNr, currValue);
          return;
        }
      }
      lg.warn("unknown spinner recived!");
    }
  }
}
