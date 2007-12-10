/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Subhash
 */
public class RESTView extends JPanel implements View {
    
    private JFileChooser jfc = new JFileChooser();
    
    private JRadioButton jrb_req_get = new JRadioButton("GET");
    private JRadioButton jrb_req_post = new JRadioButton("POST");
    private JRadioButton jrb_req_put = new JRadioButton("PUT");
    private JRadioButton jrb_req_delete = new JRadioButton("DELETE");
    private JRadioButton jrb_req_head = new JRadioButton("HEAD");
    private JRadioButton jrb_req_options = new JRadioButton("OPTIONS");
    private JRadioButton jrb_req_trace = new JRadioButton("TRACE");
    
    private JProgressBar jpb_status = new JProgressBar();
    
    private JLabel jl_status = new JLabel("RESTClient");
    private JLabel jl_url = new JLabel("URL: ");
    private JComboBox jcb_url = new JComboBox();
    
    private JButton jb_request = new JButton(" Request ");
    private JButton jb_clear = new JButton(" Clear Response ");
    
    private JTextField jtf_res_status = new JTextField();
    
    private JTextArea jta_req_body = new JTextArea();
    private JButton jb_body_content_type = new JButton("Content-type");
    private JButton jb_body_file = new JButton("Load from file");
    private JButton jb_body_params = new JButton("Insert parameters");
    private BodyContentTypeDialog jd_body_content_type;
    private JScrollPane jsp_req_body;
    private Dimension d_jsp_req_body;
    
    private JScrollPane jsp_res_body = new JScrollPane();
    private JTextArea jta_response = new JTextArea();
    
    private JTable jt_res_headers = new JTable();
    private JTable jt_req_params = new JTable();
    private JTable jt_req_body = new JTable();
    
    private TwoColumnTablePanel jp_2col_req_headers;
    
    private ParameterDialog jd_req_paramDialog;
    
    private ResponseHeaderTableModel resHeaderTableModel = new ResponseHeaderTableModel();

    // Authentication resources
    private JCheckBox jcb_auth_basic = new JCheckBox("BASIC");
    private JCheckBox jcb_auth_digest = new JCheckBox("DIGEST");
    private JCheckBox jcb_auth_preemptive = new JCheckBox("Preemptive");
    private JLabel jl_auth_host = new JLabel("<html>Host: </html>");
    private JLabel jl_auth_realm = new JLabel("<html>Realm: </html>");
    private JLabel jl_auth_username = new JLabel("<html>Username: <font color=red>*</font></html>");
    private JLabel jl_auth_password = new JLabel("<html>Password: <font color=red>*</font></html>");
    private final int auth_text_size = 20;
    private JTextField jtf_auth_host = new JTextField(auth_text_size);
    private JTextField jtf_auth_realm = new JTextField(auth_text_size);
    private JTextField jtf_auth_username = new JTextField(auth_text_size);
    private JPasswordField jpf_auth_password = new JPasswordField(auth_text_size);
    
    private ErrorDialog errorDialog;
    private final RESTView view;
    private final JFrame frame;
    
    protected RESTView(final JFrame frame){
        this.frame = frame;
        init();
        view = this;
    }
    
    private JTabbedPane initJTPRequest(){
        JTabbedPane jtp = new JTabbedPane();
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(jrb_req_get);
        bg.add(jrb_req_post);
        bg.add(jrb_req_put);
        bg.add(jrb_req_delete);
        bg.add(jrb_req_head);
        bg.add(jrb_req_options);
        bg.add(jrb_req_trace);
        
        // Default selected button
        jrb_req_get.setSelected(true);
        
        // Mnemonic
        jrb_req_get.setMnemonic('g');
        jrb_req_post.setMnemonic('p');
        jrb_req_put.setMnemonic('t');
        jrb_req_delete.setMnemonic('d');
        jrb_req_head.setMnemonic('h');
        jrb_req_options.setMnemonic('o');
        jrb_req_trace.setMnemonic('e');
        
        ActionListener jrbAL = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(jrb_req_post.isSelected() || jrb_req_put.isSelected()){
                    reqBodyToggle(true);
                }
                else{
                    reqBodyToggle(false);
                }
            }
        };
        
        jrb_req_get.addActionListener(jrbAL);
        jrb_req_post.addActionListener(jrbAL);
        jrb_req_put.addActionListener(jrbAL);
        jrb_req_delete.addActionListener(jrbAL);
        jrb_req_head.addActionListener(jrbAL);
        jrb_req_options.addActionListener(jrbAL);
        jrb_req_trace.addActionListener(jrbAL);
        
        JPanel jp_method_encp = new JPanel();
        jp_method_encp.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel jp_method = new JPanel();
        jp_method.setBorder(new TitledBorder("HTTP Method"));
        jp_method.setLayout(new GridLayout(4, 2));
        jp_method.add(jrb_req_get);
        jp_method.add(jrb_req_post);
        jp_method.add(jrb_req_put);
        jp_method.add(jrb_req_delete);
        jp_method.add(jrb_req_head);
        jp_method.add(jrb_req_options);
        jp_method.add(jrb_req_trace);
        jp_method_encp.add(jp_method);
        jtp.addTab("Method", jp_method_encp);
        
        // Headers Tab
        jp_2col_req_headers = new TwoColumnTablePanel(new String[]{"Header", "Value"}, frame);
        jtp.addTab("Headers", jp_2col_req_headers);
        
        // Body Tab
        reqBodyToggle(false); // disable control by default
        JPanel jp_body = new JPanel();
        jp_body.setLayout(new BorderLayout());
        JPanel jp_body_north = new JPanel();
        jp_body_north.setLayout(new FlowLayout(FlowLayout.CENTER));
        jb_body_content_type.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jd_body_content_type.showMe();
                    }
                });
            }
        });
        jp_body_north.add(jb_body_content_type);
        jb_body_file.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                jb_body_fileActionPerformed(event);
            }
        });
        jp_body_north.add(jb_body_file);
        jb_body_params.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                jb_body_paramActionPerformed(event);
            }
        });
        jp_body_north.add(jb_body_params);
        jp_body.add(jp_body_north, BorderLayout.NORTH);
        JPanel jp_body_center = new JPanel();
        jp_body_center.setLayout(new GridLayout(1, 1));
        jsp_req_body = new JScrollPane(jta_req_body);
        jp_body_center.add(jsp_req_body);
        jp_body.add(jp_body_center, BorderLayout.CENTER);
        jtp.addTab("Body", jp_body);
        
        // Auth Tab
        JPanel jp_auth = new JPanel();
        jp_auth.setLayout(new BorderLayout());
        JPanel jp_auth_west = new JPanel();
        jp_auth_west.setLayout(new BorderLayout());
        JPanel jp_auth_west_center = new JPanel();
        jp_auth_west_center.setBorder(new TitledBorder("Auth Type"));
        jp_auth_west_center.setLayout(new GridLayout(2,1));
        jcb_auth_basic.setSelected(false);
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                auth_enableActionPerformed(event);
            }
        };
        jcb_auth_basic.addActionListener(action);
        jcb_auth_digest.addActionListener(action);
        jp_auth_west_center.add(jcb_auth_basic);
        jp_auth_west_center.add(jcb_auth_digest);
        jp_auth_west.add(jp_auth_west_center, BorderLayout.CENTER);
        JPanel jp_auth_west_south = new JPanel();
        jp_auth_west_south.setBorder(BorderFactory.createTitledBorder("Preemptive?"));
        jp_auth_west_south.setLayout(new GridLayout(1, 1));
        jcb_auth_preemptive.setSelected(true);
        jcb_auth_preemptive.setEnabled(false);
        jp_auth_west_south.add(jcb_auth_preemptive);
        jp_auth_west.add(jp_auth_west_south, BorderLayout.SOUTH);
        jp_auth.add(jp_auth_west, BorderLayout.WEST);
        JPanel jp_auth_center = new JPanel();
        jp_auth_center.setBorder(new TitledBorder("Details"));
        jp_auth_center.setLayout(new BorderLayout());
        JPanel jp_auth_center_west = new JPanel();
        jp_auth_center_west.setLayout(new GridLayout(4, 1, 5, 5));
        jl_auth_host.setEnabled(false);
        jl_auth_realm.setEnabled(false);
        jl_auth_username.setEnabled(false);
        jl_auth_password.setEnabled(false);
        jp_auth_center_west.add(jl_auth_host);
        jp_auth_center_west.add(jl_auth_realm);
        jp_auth_center_west.add(jl_auth_username);
        jp_auth_center_west.add(jl_auth_password);
        jp_auth_center.add(jp_auth_center_west, BorderLayout.WEST);
        JPanel jp_auth_center_center = new JPanel();
        jp_auth_center_center.setLayout(new GridLayout(4, 1, 5, 5));
        jtf_auth_host.setEnabled(false);
        jtf_auth_realm.setEnabled(false);
        jtf_auth_username.setEnabled(false);
        jpf_auth_password.setEnabled(false);
        jp_auth_center_center.add(jtf_auth_host);
        jp_auth_center_center.add(jtf_auth_realm);
        jp_auth_center_center.add(jtf_auth_username);
        jp_auth_center_center.add(jpf_auth_password);
        jp_auth_center.add(jp_auth_center_center, BorderLayout.CENTER);
        jp_auth.add(jp_auth_center, BorderLayout.CENTER);
        JPanel jp_auth_encp = new JPanel();
        jp_auth_encp.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp_auth_encp.add(jp_auth);
        jtp.addTab("Authentication", jp_auth_encp);
        
        return jtp;
    }
    
    private JTabbedPane initJTPResponse(){
        JTabbedPane jtp = new JTabbedPane();
        
        // Header Tab
        JPanel jp_headers = new JPanel();
        jp_headers.setLayout(new BorderLayout());
        
        // Header Tab: Status Line Header
        JPanel jp_headers_status = new JPanel();
        jp_headers_status.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel jl_res_statusLine = new JLabel("Status: ");
        jp_headers_status.add(jl_res_statusLine);
        jtf_res_status.setColumns(35);
        jtf_res_status.setEditable(false);
        jp_headers_status.add(jtf_res_status);
        jp_headers.add(jp_headers_status, BorderLayout.NORTH);
        
        // Header Tab: Other Headers
        JPanel jp_headers_others = new JPanel();
        jp_headers_others.setLayout(new GridLayout(1, 1));
        jt_res_headers.setModel(resHeaderTableModel);
        JScrollPane jsp = new JScrollPane(jt_res_headers);
        Dimension d = jsp.getPreferredSize();
        d.height = d.height / 2;
        jsp.setPreferredSize(d);
        jp_headers_others.add(jsp);
        jp_headers.add(jp_headers_others, BorderLayout.CENTER);
        jtp.addTab("Response Headers", jp_headers);
        
        JPanel jp_body = new JPanel();
        jp_body.setLayout(new GridLayout(1,1));
        jta_response.setEditable(false);
        jsp_res_body = new JScrollPane(jta_response);
        jp_body.add(jsp_res_body);
        JPanel jp_body_encp = new JPanel();
        jp_body_encp.setBorder(BorderFactory.createEmptyBorder());
        jp_body_encp.setLayout(new GridLayout(1, 1));
        jp_body_encp.add(jp_body);
        jtp.addTab("Response Body", jp_body_encp);
        return jtp;
    }
    
    private JPanel initNorth(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jl_url.setLabelFor(jcb_url);
        jl_url.setDisplayedMnemonic('u');
        jp.add(jl_url, BorderLayout.WEST);
        Dimension d = jcb_url.getSize();
        jcb_url.setSize(85, d.height);
        jcb_url.setEditable(true);
        jcb_url.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_urlActionPerformed(evt);
            }
        });
        jp.add(jcb_url, BorderLayout.CENTER);
        JPanel jp_encp = new JPanel();
        // top, left, bottom, right:
        jp_encp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jp_encp.setLayout(new GridLayout(1,1));
        jp_encp.add(jp);
        return jp_encp;
    }
    
    private JPanel initCenter(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp.add(initJTPRequest(), BorderLayout.NORTH);
        
        JPanel jp_buttons = new JPanel();
        jp_buttons.setLayout(new BorderLayout(5, 5));
        jb_request.setMnemonic('r');
        jb_request.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                jb_requestActionPerformed(event);
            }
        });
        jb_clear.setMnemonic('c');
        jb_clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                clear();
            }
        });
        jp_buttons.add(jb_request, BorderLayout.CENTER);
        jp_buttons.add(jb_clear, BorderLayout.EAST);
        JPanel jp_buttons_encp = new JPanel();
        jp_buttons_encp.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        jp_buttons_encp.setLayout(new GridLayout(1, 1));
        jp_buttons_encp.add(jp_buttons);
        jp.add(jp_buttons_encp, BorderLayout.CENTER);
        
        jp.add(initJTPResponse(), BorderLayout.SOUTH);
        JPanel jp_encp = new JPanel();
        jp_encp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jp_encp.setLayout(new GridLayout(1,1));
        jp_encp.add(jp);
        return jp_encp;
    }
    
    private JPanel initSouth(){
        JPanel jp = new JPanel();
        jp.setBorder(new BevelBorder(1));
        jp.setLayout(new GridLayout(1, 2));
        Font font = jl_status.getFont();
        String fontName = font.getName();
        int fontSize = font.getSize();
        Font newFont = new Font(fontName, Font.PLAIN, fontSize);
        jl_status.setFont(newFont);
        jp.add(jl_status);
        JPanel jp_south_jcb = new JPanel();
        jp_south_jcb.setLayout(new FlowLayout(FlowLayout.RIGHT));
        Dimension d = jpb_status.getPreferredSize();
        d.height = d.height - 2;
        jpb_status.setPreferredSize(d);
        jpb_status.setIndeterminate(true);
        jpb_status.setVisible(false);
        jp_south_jcb.add(jpb_status);
        jp.add(jp_south_jcb);
        return jp;
    }
    
    private void init(){
        // Initialize the errorDialog
        errorDialog = new ErrorDialog(frame);
        
        // Initialize parameter dialog
        ParameterView pv = new ParameterView(){
            public void setParameter(final String params) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jta_req_body.setText(params);
                    }
                });
            }
            
        };
        jd_req_paramDialog = new ParameterDialog(frame, pv);
        
        // Initialize jd_body_content_type
        jd_body_content_type = new BodyContentTypeDialog(frame);
        
        this.setLayout(new BorderLayout());
        
        this.add(initNorth(), BorderLayout.NORTH);
        this.add(initCenter(), BorderLayout.CENTER);
        this.add(initSouth(), BorderLayout.SOUTH);
    }

    private void jb_requestActionPerformed(java.awt.event.ActionEvent evt) {                                           
        
        List<String> errors = validateForRequest();
        if(errors.size()!=0){
            StringBuffer sb = new StringBuffer();
            sb.append("<html><ul>");
            for(String error: errors){
                sb.append("<li>").append(error).append("</li>");
            }
            sb.append("</ul></html>");
            JOptionPane.showMessageDialog(frame,
                sb.toString(),
                "Validation error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        RequestBean request = new RequestBean();
        boolean authEnabled = false;
        
        if(jcb_auth_basic.isSelected()){
            request.addAuthMethod("BASIC");
            authEnabled = true;
        }
        if(jcb_auth_digest.isSelected()){
            request.addAuthMethod("DIGEST");
            authEnabled = true;
        }
        
        if(authEnabled){
            // Pass the credentials
            String uid = jtf_auth_username.getText();
            char[] pwd = jpf_auth_password.getPassword();
            
            String realm = jtf_auth_realm.getText();
            String host = jtf_auth_host.getText();
            boolean preemptive = jcb_auth_preemptive.isSelected();
            
            request.setAuthPreemptive(preemptive);
            request.setAuthUsername(uid);
            request.setAuthPassword(pwd);
            request.setAuthRealm(realm);
            request.setAuthHost(host);
        }
        
        String url = (String)jcb_url.getSelectedItem();
        try{
            request.setUrl(new URL(url));
        }
        catch(MalformedURLException ex){
            assert true: "Should not come here as validation logic checks this.";
        }
        if(jrb_req_get.isSelected()){
            request.setMethod("GET");
        }
        else if(jrb_req_head.isSelected()){
            request.setMethod("HEAD");
        }
        else if(jrb_req_post.isSelected()){
            request.setMethod("POST");
        }
        else if(jrb_req_put.isSelected()){
            request.setMethod("PUT");
        }
        else if(jrb_req_delete.isSelected()){
            request.setMethod("DELETE");
        }
        else if(jrb_req_options.isSelected()){
            request.setMethod("OPTIONS");
        }
        else if(jrb_req_trace.isSelected()){
            request.setMethod("TRACE");
        }
        
        // Get request headers
        Object[][] header_data = jp_2col_req_headers.getTableModel().getData();
        if(header_data.length > 0){
            for(int i=0; i<header_data.length; i++){
                String key = (String)header_data[i][0];
                String value = (String)header_data[i][1];
                request.addHeader(key, value);
            }
        }
        
        // EntityEnclosing method specific
        if(jrb_req_post.isSelected() || jrb_req_put.isSelected()){
            // Get request body
            String req_body = jta_req_body.getText();
            if(!Util.isStrEmpty(req_body)){
                String req_content_type = jd_body_content_type.getContentType();
                String req_char_set = jd_body_content_type.getCharSet();
                ReqEntityBean body = new ReqEntityBean(req_body,
                        req_content_type,
                        req_char_set);
                request.setBody(body);
            }
        }
        

        clear();
        new HTTPRequestThread(request, view).start();
    }                                          

    // This is accessed by the Thread. Don't make it private.
    public void doResponse(final ResponseBean response){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jtf_res_status.setText(response.getStatusLine());
                String responseBody = response.getResponseBody();
                if(responseBody != null){
                    Dimension d = jta_response.getPreferredScrollableViewportSize();
                    jta_response.setText(responseBody);
                    jsp_res_body.setPreferredSize(d);
                }
                else{
                    jta_response.setText("");
                }
                ResponseHeaderTableModel model = (ResponseHeaderTableModel)jt_res_headers.getModel();
                model.setHeader(response.getHeaders());
                jb_request.requestFocus();
                // frame.pack();
            }
        });
    }
    
    public void doStart(RequestBean request){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jpb_status.setVisible(true);
                jb_request.setEnabled(false);
            }
        });
    }
    
    // This is accessed by the Thread. Don't make it private.
    public void doEnd(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jpb_status.setVisible(false);
                jb_request.setEnabled(true);
            }
        });
    }
    
    private void clear(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jtf_res_status.setText("");
                jta_response.setText("");
                ResponseHeaderTableModel model = (ResponseHeaderTableModel)jt_res_headers.getModel();
                model.setHeader(null);
            }
        });
    }
    
    private void jcb_urlActionPerformed(final ActionEvent event){
        if("comboBoxChanged".equals(event.getActionCommand())){
            return;
        }
        final Object item = jcb_url.getSelectedItem();
        final int count = jcb_url.getItemCount();
        final LinkedList l = new LinkedList();
        for(int i=0; i<count; i++){
            l.add(jcb_url.getItemAt(i));
        }
        if(l.contains(item)){ // Item already present
            // Remove and add to bring it to the top
            // l.remove(item);
            // l.addFirst(item);
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    // System.out.println("Removing and inserting at top");
                    jcb_url.removeItem(item);
                    jcb_url.insertItemAt(item, 0);
                }
            });
        }
        else{ // Add new item
            // The total number of items should not exceed 20
            if(count > 19){
                // Remove last item to give place
                // to new one
                //l.removeLast();
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        jcb_url.removeItemAt(count - 1);
                    }
                });
            }
            //l.addFirst(item);
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    // System.out.println("Inserting at top");
                    jcb_url.insertItemAt(item, 0);
                }
            });
        }
    }
    
    private void auth_enableActionPerformed(final ActionEvent event){
        if(jcb_auth_basic.isSelected() || jcb_auth_digest.isSelected()){
            authToggle(true);
        }
        else{
            authToggle(false);
        }
    }
    
    private void authToggle(final boolean boo){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jcb_auth_preemptive.setEnabled(boo);
                jtf_auth_host.setEnabled(boo);
                jtf_auth_realm.setEnabled(boo);
                jtf_auth_username.setEnabled(boo);
                jpf_auth_password.setEnabled(boo);

                // Disable/enable labels:
                jl_auth_host.setEnabled(boo);
                jl_auth_realm.setEnabled(boo);
                jl_auth_username.setEnabled(boo);
                jl_auth_password.setEnabled(boo);
            }
        });
    }
    
    private void jb_body_fileActionPerformed(ActionEvent event){
        if(!canSetReqBodyText()){
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int returnVal = jfc.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = jfc.getSelectedFile();
                    if(!f.canRead()){
                        JOptionPane.showMessageDialog(frame,
                                "File not readable: " + f.getAbsolutePath(),
                                "IO Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Get text from file and set
                    try{
                        String body = Util.getStringFromFile(f);
                        // setJTAReqBodyDimension();
                        jta_req_body.setText(body);
                    }
                    catch(IOException ex){
                        JOptionPane.showMessageDialog(frame,
                                "IO Error: " + ex.getMessage(),
                                "IO Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
    
    private void jb_body_paramActionPerformed(ActionEvent event){
        if(!canSetReqBodyText()){
            return;
        }
        checkAndSetParameterContentType();
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jd_req_paramDialog.setLocationRelativeTo(frame);
                jd_req_paramDialog.setVisible(true);
            }
        });
    }
    
    private boolean canSetReqBodyText(){
        if(Util.isStrEmpty(jta_req_body.getText())){
            return true;
        }
        else{
            int response = JOptionPane.showConfirmDialog(frame,
                    "Body text exists. Erase?",
                    "Erase?",
                    JOptionPane.YES_NO_OPTION);
            if(response == JOptionPane.YES_OPTION){
                return true;
            }
        }
        return false;
    }
    
    
    private void checkAndSetParameterContentType(){
        if(!jd_body_content_type.getContentType().equals(BodyContentTypeDialog.PARAM_CONTENT_TYPE)
                || !jd_body_content_type.getCharSet().equals(BodyContentTypeDialog.PARAM_CHARSET)){
            int status = JOptionPane.showConfirmDialog(frame,
                    "<html>For parameter the Content-type and Charset needs <br>" +
                    "to be `" + BodyContentTypeDialog.PARAM_CONTENT_TYPE +
                    "' and `"+
                    BodyContentTypeDialog.PARAM_CHARSET +
                    "' respectively.<br>"+
                    "Do you want to set this option?</html>",
                    "Parameter Content-type and Charset",
                    JOptionPane.YES_NO_OPTION);
            if(status == JOptionPane.YES_OPTION){
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jd_body_content_type.setContentType(BodyContentTypeDialog.PARAM_CONTENT_TYPE);
                        jd_body_content_type.setCharSet(BodyContentTypeDialog.PARAM_CHARSET);
                    }
                });
            }
        }
    }
    
    private void reqBodyToggle(final boolean boo){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setJTAReqBodyDimension();
                
                jta_req_body.setEnabled(boo);
                jb_body_content_type.setEnabled(boo);
                jb_body_file.setEnabled(boo);
                jb_body_params.setEnabled(boo);
            }
        });
    }
    
    private void setJTAReqBodyDimension(){
        // The TextArea was re-drawing to a bigger size
        // when large text was placed. This check is for
        // avoiding that.
        // This method will be invoked from calls that are running
        // inside SwingUtilities.invokeLater()
        if(d_jsp_req_body == null){
            Dimension d = jta_req_body.getPreferredScrollableViewportSize();
            d_jsp_req_body = d;
        }
        jsp_req_body.setPreferredSize(d_jsp_req_body);
    }
    
    public void doError(final String error){
        errorDialog.showError(error);
    }
    
    private List<String> validateForRequest(){
        List<String> errors = new ArrayList<String>();
        Object o = null;
        String str = null;
        str = (String)jcb_url.getSelectedItem();
        if(Util.isStrEmpty(str)){
            errors.add("URL field is empty.");
        }
        else{
            try{
                new URL(str);
            }
            catch(MalformedURLException ex){
                errors.add("URL is malformed.");
            }
        }
        if(jcb_auth_basic.isSelected() || jcb_auth_digest.isSelected()){
            if(Util.isStrEmpty(jtf_auth_username.getText())){
                errors.add("Username is empty.");
            }
            if(Util.isStrEmpty(new String(jpf_auth_password.getPassword()))){
                errors.add("Password is empty.");
            }
        }
        if(jrb_req_post.isSelected() || jrb_req_put.isSelected()){
            // Get request body
            String req_body = jta_req_body.getText();
            if(!Util.isStrEmpty(req_body)){
                String req_content_type = jd_body_content_type.getContentType();
                String req_char_set = jd_body_content_type.getCharSet();
                if(Util.isStrEmpty(req_content_type)
                        || Util.isStrEmpty(req_char_set)){
                    errors.add("Body content is set, but `Content-type' and/or `Char-set' not set.");
                }
            }
        }
        return errors;
    }
}
