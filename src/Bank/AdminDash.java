package Bank;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;


public class AdminDash extends JFrame {

    // PALETTE
    static final Color PAGE_BG   = new Color(245, 246, 250);
    static final Color SIDE_BG   = Color.WHITE;
    static final Color CARD      = Color.WHITE;
    static final Color CARD2     = new Color(251, 251, 254);
    static final Color BDR       = new Color(226, 228, 233);
    static final Color BDR2      = new Color(203, 207, 216);

    static final Color T1        = new Color(15,  23,  42);
    static final Color T2        = new Color(71,  85,  105);
    static final Color T3        = new Color(148, 163, 184);

    static final Color CR        = new Color(185, 28,  28);   // crimson
    static final Color CR_BG     = new Color(254, 242, 242);
    static final Color CR_BD     = new Color(252, 165, 165);

    static final Color GR        = new Color(4,   120, 87);   // emerald
    static final Color GR_BG     = new Color(236, 253, 245);
    static final Color GR_BD     = new Color(110, 231, 183);

    static final Color AM        = new Color(161, 75,  8);    // amber
    static final Color AM_BG     = new Color(255, 251, 235);
    static final Color AM_BD     = new Color(252, 211, 77);

    static final Color IN        = new Color(67,  56,  202);  // indigo
    static final Color IN_BG     = new Color(238, 242, 255);
    static final Color IN_BD     = new Color(165, 180, 252);

    static final Color VT        = new Color(109, 40,  217);  // violet
    static final Color VT_BG     = new Color(245, 243, 255);
    static final Color VT_BD     = new Color(196, 181, 253);

    static final Color SL        = new Color(100, 116, 139);  // slate

    // STATE
    private JPanel     content;
    private JPanel     sidebar; 
    private CardLayout cards;
    private String     view = "Dashboard";
    private int[]      stock = new int[8];
    private String[]   GRP = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};
    private int        lowCnt, pendCnt, donorCnt, alertsCnt;
    private List<TxRow> txList = new ArrayList<>();
    private JLabel     clockLbl;

    // NEPAL ROAD GRAPH using km nearest city
    private static final Map<String, Map<String,Integer>> GRAPH = new LinkedHashMap<>();
    static {
        G("Kathmandu","Lalitpur",5);  G("Kathmandu","Bhaktapur",13);
        G("Kathmandu","Chitwan",150); G("Kathmandu","Pokhara",200);
        G("Kathmandu","Hetauda",100); G("Kathmandu","Dharan",400);
        G("Pokhara","Baglung",75);    G("Pokhara","Palpa",90);
        G("Pokhara","Syangja",50);    G("Pokhara","Mustang",195);
        G("Palpa","Butwal",45);       G("Palpa","Syangja",60);
        G("Palpa","Gulmi",40);        G("Butwal","Kapilvastu",70);
        G("Butwal","Rupandehi",20);   G("Butwal","Bhairahawa",20);
        G("Chitwan","Hetauda",50);    G("Chitwan","Makwanpur",60);
        G("Hetauda","Makwanpur",20);  G("Lalitpur","Makwanpur",65);
        G("Dharan","Biratnagar",30);  G("Biratnagar","Jhapa",60);
        G("Dharan","Dhankuta",40);    G("Nepalgunj","Banke",5);
        G("Nepalgunj","Surkhet",110); G("Rupandehi","Kapilvastu",60);
    }
    static void G(String a, String b, int d) {
        GRAPH.computeIfAbsent(a, k -> new LinkedHashMap<>()).put(b, d);
        GRAPH.computeIfAbsent(b, k -> new LinkedHashMap<>()).put(a, d);
    }

    // ── DIJKSTRA
    static Map<String,Integer> dijkstra(String src) {
        List<String> nodes = new ArrayList<>(GRAPH.keySet());
        Map<String,Integer> dist = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        String s = src;
        for (String n : nodes) if (n.equalsIgnoreCase(src)) { s = n; break; }
        for (String n : nodes) dist.put(n, Integer.MAX_VALUE);
        dist.put(s, 0); pq.offer(new int[]{nodes.indexOf(s), 0});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll(); String cn = nodes.get(cur[0]);
            if (cur[1] > dist.getOrDefault(cn, Integer.MAX_VALUE)) continue;
            for (Map.Entry<String,Integer> nb : GRAPH.getOrDefault(cn, new HashMap<>()).entrySet()) {
                int nd = cur[1] + nb.getValue();
                if (nd < dist.getOrDefault(nb.getKey(), Integer.MAX_VALUE)) {
                    dist.put(nb.getKey(), nd);
                    pq.offer(new int[]{nodes.indexOf(nb.getKey()), nd});
                }
            }
        }
        return dist;
    }

    private int resDist(Map<String,Integer> dist, String city) {
        if (city == null) return Integer.MAX_VALUE;
        for (Map.Entry<String,Integer> e : dist.entrySet())
            if (e.getKey().equalsIgnoreCase(city.trim())) return e.getValue();
        return Integer.MAX_VALUE;
    }

    
    public AdminDash() {
        setTitle("LifeLine Blood Bank — Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1280, 800));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        try { UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); } catch (Exception ignored) {}

        
        UIManager.put("Panel.background",               PAGE_BG);
        UIManager.put("ScrollPane.background",          PAGE_BG);
        UIManager.put("Viewport.background",            CARD);
        UIManager.put("TextField.background",           Color.WHITE);
        UIManager.put("TextField.foreground",           T1);
        UIManager.put("TextField.caretForeground",      T1);
        UIManager.put("TextField.selectionBackground",  IN_BG);
        UIManager.put("TextField.selectionForeground",  T1);
        UIManager.put("TextField.border",               new CompoundBorder(new LineBorder(BDR2, 1, true), new EmptyBorder(7, 12, 7, 12)));
        UIManager.put("TextArea.background",            new Color(248, 249, 251));
        UIManager.put("TextArea.foreground",            T2);
        UIManager.put("TextArea.caretForeground",       T1);
        UIManager.put("TextArea.selectionBackground",   IN_BG);
        UIManager.put("TextArea.border",                new EmptyBorder(10, 12, 10, 12));
        UIManager.put("ComboBox.background",            Color.WHITE);
        UIManager.put("ComboBox.foreground",            T1);
        UIManager.put("ComboBox.selectionBackground",   IN_BG);
        UIManager.put("ComboBox.selectionForeground",   T1);
        UIManager.put("ComboBox.buttonBackground",      new Color(241, 242, 246));
        UIManager.put("ComboBox.buttonShadow",          BDR);
        UIManager.put("ComboBox.buttonDarkShadow",      BDR2);
        UIManager.put("ComboBox.buttonHighlight",       Color.WHITE);
        UIManager.put("ComboBox.disabledBackground",    new Color(241, 242, 246));
        UIManager.put("ComboBox.disabledForeground",    T3);
        UIManager.put("ComboBox.border",                new LineBorder(BDR2, 1));
        UIManager.put("List.background",                Color.WHITE);
        UIManager.put("List.foreground",                T1);
        UIManager.put("List.selectionBackground",       IN_BG);
        UIManager.put("List.selectionForeground",       T1);
        UIManager.put("Table.background",               Color.WHITE);
        UIManager.put("Table.foreground",               T1);
        UIManager.put("Table.selectionBackground",      IN_BG);
        UIManager.put("Table.selectionForeground",      T1);
        UIManager.put("Table.gridColor",                BDR);
        UIManager.put("TableHeader.background",         new Color(248, 249, 251));
        UIManager.put("TableHeader.foreground",         T2);
        UIManager.put("TableHeader.cellBorder",         new MatteBorder(0, 0, 2, 1, BDR));
        UIManager.put("Button.background",              Color.WHITE);
        UIManager.put("Button.foreground",              T1);
        UIManager.put("Button.shadow",                  BDR);
        UIManager.put("Button.darkShadow",              BDR2);
        UIManager.put("Button.highlight",               Color.WHITE);
        UIManager.put("Button.light",                   CARD2);
        UIManager.put("Button.border",                  new EmptyBorder(8, 16, 8, 16));
        UIManager.put("Button.focus",                   new Color(0,0,0,0));
        UIManager.put("TabbedPane.background",          CARD);
        UIManager.put("TabbedPane.foreground",          T2);
        UIManager.put("TabbedPane.selected",            CARD);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(2,0,0,0));
        UIManager.put("TabbedPane.tabAreaBackground",   PAGE_BG);
        UIManager.put("TabbedPane.light",               CARD);
        UIManager.put("TabbedPane.highlight",           CARD);
        UIManager.put("TabbedPane.shadow",              BDR);
        UIManager.put("TabbedPane.darkShadow",          BDR2);
        UIManager.put("TabbedPane.selectedForeground",  T1);
        UIManager.put("OptionPane.background",          Color.WHITE);
        UIManager.put("OptionPane.messageForeground",   T1);
        UIManager.put("OptionPane.buttonAreaBorder",    new EmptyBorder(8, 8, 8, 8));
        UIManager.put("Label.foreground",               T1);
        UIManager.put("ScrollBar.background",           PAGE_BG);
        UIManager.put("ScrollBar.thumb",                BDR2);
        UIManager.put("ScrollBar.thumbDarkShadow",      BDR2);
        UIManager.put("ScrollBar.thumbHighlight",       BDR);
        UIManager.put("ScrollBar.thumbShadow",          BDR);
        UIManager.put("ScrollBar.track",                PAGE_BG);
        UIManager.put("ScrollBar.trackHighlight",       PAGE_BG);

        getContentPane().setBackground(PAGE_BG);
        try {
            loadData();
            SwingUtilities.invokeLater(this::autoAlert);
            new Timer(5 * 60 * 1000, e -> autoAlert()).start();

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(PAGE_BG);
            sidebar = buildSidebar(); 
            root.add(sidebar, BorderLayout.WEST);
            cards = new CardLayout();
            content = new JPanel(cards);
            content.setOpaque(false);
            content.setBorder(new EmptyBorder(28, 36, 28, 36));
            render();
            root.add(content, BorderLayout.CENTER);
            setContentPane(root);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Startup error:\n" + ex.getMessage());
        }
    }

  
    // AUTO-ALERT ENGINE
    
    private void autoAlert() {
        new Thread(() -> {
            try (Connection con = DBConnection.connect()) {
                if (con == null) return;
                for (int i = 0; i < 8; i++) {
                    if (stock[i] >= 10) continue;
                    String bg = GRP[i], sev = stock[i] < 5 ? "CRITICAL" : "LOW";
                    Map<String,Integer> dists = dijkstra("Kathmandu");
                    List<DR> pool = new ArrayList<>();
                    try {
                        PreparedStatement ps = con.prepareStatement(
                            "SELECT id,name,city FROM users WHERE blood_group=? AND role='User' AND city IS NOT NULL AND city!=''");
                        ps.setString(1, bg); ResultSet rs = ps.executeQuery();
                        while (rs.next()) pool.add(new DR(rs.getInt(1),rs.getString(2),rs.getString(3),resDist(dists,rs.getString(3)),"users"));
                    } catch (Exception ignored) {}
                    try {
                        PreparedStatement ps = con.prepareStatement(
                            "SELECT id,name,city FROM donors WHERE blood_group=? AND city IS NOT NULL AND city!=''");
                        ps.setString(1, bg); ResultSet rs = ps.executeQuery();
                        while (rs.next()) pool.add(new DR(rs.getInt(1),rs.getString(2),rs.getString(3),resDist(dists,rs.getString(3)),"donors"));
                    } catch (Exception ignored) {}
                    if (pool.isEmpty()) continue;
                    pool.sort(Comparator.comparingInt(d -> d.dist));
                    int sent = 0;
                    String msg = "[AUTO] "+sev+": "+bg+" blood is at "+stock[i]+" units. Your proximity makes you a priority. Please visit LifeLine Blood Bank urgently.";
                    for (DR d : pool) {
                        if (sent >= 3) break;
                        if (!"users".equals(d.src)) continue;
                        try {
                            PreparedStatement dup = con.prepareStatement(
                                "SELECT COUNT(*) FROM notifications WHERE user_id=? AND message LIKE ? AND created_at > DATE_SUB(NOW(),INTERVAL 24 HOUR)");
                            dup.setInt(1, d.uid); dup.setString(2, "%AUTO%"+bg+"%");
                            ResultSet dr = dup.executeQuery();
                            if (dr.next() && dr.getInt(1) > 0) continue;
                        } catch (Exception ignored) {}
                        try {
                            PreparedStatement ins = con.prepareStatement(
                                "INSERT INTO notifications(user_id,message,is_read,created_at) VALUES(?,?,0,NOW())");
                            ins.setInt(1, d.uid);
                            ins.setString(2, msg + " ("+(d.dist==Integer.MAX_VALUE?"registered donor":d.dist+" km from hub")+")");
                            ins.executeUpdate(); sent++; alertsCnt++;
                        } catch (Exception ignored) {}
                    }
                    if (sent > 0) {
                        try {
                            PreparedStatement log = con.prepareStatement(
                                "INSERT INTO system_alerts(blood_group,message,created_at) VALUES(?,?,NOW())");
                            log.setString(1, bg);
                            log.setString(2, sev+" auto-alert: "+sent+" nearest "+bg+" donors notified via Dijkstra.");
                            log.executeUpdate();
                        } catch (Exception ignored) {}
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    
  
    private void loadData() {
        lowCnt = pendCnt = donorCnt = alertsCnt = 0;
        txList.clear(); Arrays.fill(stock, 0);
        try (Connection con = DBConnection.connect()) {
            if (con == null) return;
            try {
                ResultSet rs = con.createStatement().executeQuery("SELECT blood_group,total_units FROM blood_stock_summary");
                while (rs.next()) for (int i = 0; i < 8; i++) if (GRP[i].equalsIgnoreCase(rs.getString(1))) {
                    stock[i] = Math.max(0, rs.getInt(2)); if (stock[i] < 10) lowCnt++;
                }
            } catch (Exception ignored) {}
            try { ResultSet rs=con.createStatement().executeQuery("SELECT COUNT(*) FROM blood_requests WHERE status='Pending'"); if(rs.next())pendCnt=rs.getInt(1); } catch(Exception ignored){}
            try { ResultSet rs=con.createStatement().executeQuery("SELECT COUNT(*) FROM users WHERE role='User' AND points>0"); if(rs.next())donorCnt=rs.getInt(1); } catch(Exception ignored){}
            try { ResultSet rs=con.createStatement().executeQuery("SELECT COUNT(*) FROM notifications WHERE DATE(created_at)=CURDATE()"); if(rs.next())alertsCnt=rs.getInt(1); } catch(Exception ignored){}
            try {
                ResultSet rs=con.createStatement().executeQuery(
                    "SELECT patient_name,blood_group,units_requested,status,request_date FROM blood_requests ORDER BY request_date DESC LIMIT 6");
                while(rs.next()) txList.add(new TxRow(rs.getString(1),rs.getString(2),rs.getInt(3),rs.getString(4),rs.getTimestamp(5)));
            } catch(Exception ignored){}
        } catch(Exception e){e.printStackTrace();}
    }

    private int priority(String bg, String note, Timestamp t) {
        int s=0, st=0;
        for(int i=0;i<8;i++) if(GRP[i].equalsIgnoreCase(bg)) st=stock[i];
        if(st<5) s+=50; else if(st<10) s+=25;
        String n=note!=null?note.toLowerCase():"";
        if(n.contains("icu")||n.contains("emergency")||n.contains("critical")) s+=30;
        else if(n.contains("urgent")) s+=20; else if(n.contains("surgery")) s+=15;
        if(t!=null) s+=Math.min(20,(int)((System.currentTimeMillis()-t.getTime())/3_600_000L)*2);
        return s;
    }

    private void refresh() { loadData(); render(); }
//menu items list 
    private void render() {
        content.removeAll();
        content.add(pgDashboard(),  "Dashboard");
        content.add(pgRequests(),   "Requests");
        content.add(pgStock(),      "Stock");
        content.add(pgDonors(),     "Donors");
        content.add(pgAlerts(),     "Alerts");
        content.add(pgAnalytics(),  "Analytics");
        content.revalidate(); content.repaint();
        cards.show(content, view);
    }

    
    private void refreshSidebar() {
        Container parent = sidebar.getParent();
        if (parent != null) {
            parent.remove(sidebar);
            sidebar = buildSidebar();
            parent.add(sidebar, BorderLayout.WEST);
            parent.revalidate();
            parent.repaint();
        }
    }

    
    private JPanel buildSidebar() {
        JPanel s = new JPanel(new BorderLayout());
        s.setPreferredSize(new Dimension(230, 0));
        s.setBackground(SIDE_BG);
        s.setBorder(new MatteBorder(0,0,0,1,BDR));

        JPanel top = new JPanel(); top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(24,18,16,18));

        
        JPanel brand = new JPanel(new BorderLayout(10,0)); brand.setOpaque(false);
        brand.setMaximumSize(new Dimension(9999,46)); brand.setAlignmentX(0);
        JPanel ico = new JPanel(){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CR); g2.fillOval(4,10,20,18);
                int[] px={14,5,14,23}, py={2,14,25,14}; g2.fillPolygon(px,py,4);
                g2.setColor(new Color(254,202,202,80)); g2.fillOval(1,7,26,24);
            }
        };
        ico.setOpaque(false); ico.setPreferredSize(new Dimension(30,34));
        JPanel bTxt=new JPanel(); bTxt.setLayout(new BoxLayout(bTxt,BoxLayout.Y_AXIS)); bTxt.setOpaque(false);
        JLabel bn=lbl("LifeLine",16,Font.BOLD,T1); bn.setAlignmentX(0);
        JLabel bs=lbl("Blood Bank Admin",10,Font.PLAIN,T3); bs.setAlignmentX(0);
        bTxt.add(bn); bTxt.add(bs);
        brand.add(ico,BorderLayout.WEST); brand.add(bTxt,BorderLayout.CENTER);
        top.add(brand); top.add(vgap(14));

        clockLbl=lbl(clk(),10,Font.PLAIN,T3); clockLbl.setAlignmentX(0);
        top.add(clockLbl);
        new Timer(1000,e->clockLbl.setText(clk())).start();
        top.add(vgap(14));

        JSeparator div=new JSeparator(); div.setForeground(BDR); div.setBackground(BDR);
        div.setMaximumSize(new Dimension(9999,1)); top.add(div); top.add(vgap(14));

        JLabel navLbl=lbl("MAIN MENU",18,Font.BOLD,T3);
        navLbl.setBorder(new EmptyBorder(0,2,10,0)); navLbl.setAlignmentX(0);
        top.add(navLbl);

        for(String pg:new String[]{"Dashboard","Requests","Stock","Donors","Alerts","Analytics"}) {
            top.add(navBtn(pg)); top.add(vgap(18));
        }
        s.add(top,BorderLayout.NORTH);

        // Footer
        JPanel foot=new JPanel(); foot.setOpaque(false);
        foot.setLayout(new BoxLayout(foot,BoxLayout.Y_AXIS));
        foot.setBorder(new EmptyBorder(0,16,20,16));
        JPanel sig=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0)); sig.setOpaque(false);
        sig.setMaximumSize(new Dimension(9999,24)); sig.setAlignmentX(0);
        JPanel dot=new JPanel(){
            protected void paintComponent(Graphics g){
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D)g).setColor(GR); ((Graphics2D)g).fillOval(1,4,9,9);
            }
        };
        dot.setOpaque(false); dot.setPreferredSize(new Dimension(12,18));
        sig.add(dot); sig.add(lbl("Auto-alerts running",11,Font.PLAIN,T2));
        foot.add(sig); foot.add(vgap(10));
        JButton logout=plainBtn("Sign Out",SL);
        logout.setMaximumSize(new Dimension(9999,36)); logout.setAlignmentX(0);
        logout.addActionListener(e->{dispose();new UserHome().setVisible(true);});
        foot.add(logout);
        s.add(foot,BorderLayout.SOUTH);
        return s;
    }

    // Navigation button 
    private JButton navBtn(String label) {
        boolean active=view.equals(label);
        JButton b=new JButton("  "+label);
        b.setFont(new Font("SansSerif",active?Font.BOLD:Font.PLAIN,13));
        b.setForeground(active?CR:T2);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setMaximumSize(new Dimension(9999,38)); b.setAlignmentX(0);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if(active){
            b.setBackground(CR_BG); b.setOpaque(true);
            b.setBorder(new CompoundBorder(new MatteBorder(0,3,0,0,CR),new EmptyBorder(0,9,0,0)));
        } else {
            b.setBackground(SIDE_BG); b.setOpaque(true);
            b.setBorder(new EmptyBorder(0,12,0,0));
            b.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){b.setBackground(new Color(248,249,252));}
                public void mouseExited(MouseEvent e){b.setBackground(SIDE_BG);}
            });
        }
        b.addActionListener(e->{
            view=label;
            render();
            refreshSidebar(); 
        });
        return b;
    }

    
    // DASHBOARD
   
    private JPanel pgDashboard() {
        JPanel p=new JPanel(new BorderLayout(0,20)); p.setOpaque(false);
        JPanel hdr=new JPanel(new BorderLayout()); hdr.setOpaque(false);
        JPanel hl=vBox();
        hl.add(lbl("Dashboard",26,Font.BOLD,T1));
        hl.add(vgap(3));
        hl.add(lbl("Good "+greeting()+"  ·  "+dayStr(),12,Font.PLAIN,T2));
        JButton ref=solidBtn("↻  Refresh",IN); ref.addActionListener(e->refresh());
        hdr.add(hl,BorderLayout.WEST); hdr.add(ref,BorderLayout.EAST);

        JPanel topBlock=new JPanel(new BorderLayout(0,14)); topBlock.setOpaque(false);
        topBlock.add(hdr,BorderLayout.NORTH);

        if(lowCnt>0){
            JPanel banner=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
            banner.setBackground(CR_BG);
            banner.setBorder(new CompoundBorder(new LineBorder(CR_BD,1),new EmptyBorder(11,16,11,16)));
            banner.add(lbl("⚠",14,Font.BOLD,CR));
            banner.add(lbl(lowCnt+" blood group(s) critically low — auto-alerts dispatched to nearest donors.",12,Font.PLAIN,CR));
            topBlock.add(banner,BorderLayout.CENTER);
        }

        int total=0; for(int v:stock) total+=v;
        JPanel kpi=new JPanel(new GridLayout(1,4,14,0)); kpi.setOpaque(false);
        kpi.add(kpiCard("Total Blood Units",total+"","units in stock",IN,IN_BG,IN_BD));
        kpi.add(kpiCard("Active Donors",donorCnt+"","registered donors",GR,GR_BG,GR_BD));
        kpi.add(kpiCard("Pending Requests",pendCnt+"","awaiting review",pendCnt>5?CR:AM,pendCnt>5?CR_BG:AM_BG,pendCnt>5?CR_BD:AM_BD));
        kpi.add(kpiCard("Alerts Today",alertsCnt+"","auto-dispatched",VT,VT_BG,VT_BD));
        topBlock.add(kpi,BorderLayout.SOUTH);

        JPanel bot=new JPanel(new GridLayout(1,2,18,0)); bot.setOpaque(false);
        bot.add(chartCard()); bot.add(recentCard());
        p.add(topBlock,BorderLayout.NORTH); p.add(bot,BorderLayout.CENTER);
        return p;
    }

    private JPanel kpiCard(String title, String val, String sub, Color ac, Color bgC, Color bdC) {
        JPanel c=new JPanel(); c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.setBackground(CARD);
        c.setBorder(new CompoundBorder(new MatteBorder(0,4,0,0,ac),new EmptyBorder(18,18,18,18)));
        c.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){c.setBackground(bgC);}
            public void mouseExited(MouseEvent e){c.setBackground(CARD);}
        });
        JLabel tl=lbl(title,11,Font.BOLD,T3); tl.setAlignmentX(0);
        JLabel vl=lbl(val,34,Font.BOLD,ac); vl.setAlignmentX(0);
        JLabel sl=lbl(sub,11,Font.PLAIN,T3); sl.setAlignmentX(0);
        c.add(tl); c.add(vgap(6)); c.add(vl); c.add(vgap(4)); c.add(sl);
        return c;
    }

    private JPanel chartCard() {
        JPanel c=card(); c.setLayout(new BorderLayout());
        JPanel h=new JPanel(new BorderLayout()); h.setOpaque(false); h.setBorder(new EmptyBorder(0,0,12,0));
        h.add(lbl("Blood Stock Overview",14,Font.BOLD,T1),BorderLayout.WEST);
        h.add(lbl("units by group",11,Font.PLAIN,T3),BorderLayout.EAST);
        c.add(h,BorderLayout.NORTH); c.add(new StockChart(),BorderLayout.CENTER);
        return c;
    }

    private JPanel recentCard() {
        JPanel c=card(); c.setLayout(new BorderLayout());
        JLabel t=lbl("Recent Requests",14,Font.BOLD,T1); t.setBorder(new EmptyBorder(0,0,12,0));
        c.add(t,BorderLayout.NORTH);
        JPanel list=new JPanel(); list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS)); list.setOpaque(false);
        if(txList.isEmpty()) list.add(lbl("No recent requests",12,Font.PLAIN,T3));
        else for(TxRow r:txList){
            list.add(txWidget(r));
            JSeparator sep=new JSeparator(); sep.setForeground(BDR); sep.setMaximumSize(new Dimension(9999,1)); list.add(sep);
        }
        JScrollPane sc=new JScrollPane(list); sc.setBorder(null); sc.setOpaque(false); sc.getViewport().setOpaque(false);
        sc.getViewport().setBackground(CARD);
        c.add(sc,BorderLayout.CENTER); return c;
    }

    private JPanel txWidget(TxRow r) {
        JPanel p=new JPanel(new BorderLayout(12,0)); p.setOpaque(false);
        p.setMaximumSize(new Dimension(9999,52)); p.setBorder(new EmptyBorder(7,2,7,2));
        Color dc=r.status.equalsIgnoreCase("Approved")?GR:r.status.equalsIgnoreCase("Pending")?AM:T3;
        JPanel dot=new JPanel(){
            protected void paintComponent(Graphics g){
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D)g).setColor(dc); ((Graphics2D)g).fillOval(0,5,10,10);
            }
        };
        dot.setOpaque(false); dot.setPreferredSize(new Dimension(12,22));
        JPanel info=new JPanel(); info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS)); info.setOpaque(false);
        info.add(lbl(r.name+" · "+r.bg+" ("+r.units+" units)",12,Font.BOLD,T1));
        info.add(lbl(r.status+" · "+fdate(r.date),11,Font.PLAIN,T3));
        JLabel badge=new JLabel(r.status);
        badge.setFont(new Font("SansSerif",Font.BOLD,9)); badge.setForeground(dc);
        badge.setBackground(r.status.equalsIgnoreCase("Approved")?GR_BG:r.status.equalsIgnoreCase("Pending")?AM_BG:new Color(241,245,249));
        badge.setOpaque(true); badge.setBorder(new EmptyBorder(3,8,3,8));
        p.add(dot,BorderLayout.WEST); p.add(info,BorderLayout.CENTER); p.add(badge,BorderLayout.EAST);
        return p;
    }

    // REQUESTS
   
    private JPanel pgRequests() {
        JPanel p=new JPanel(new BorderLayout(0,0)); p.setOpaque(false);

        JPanel hdrCard=new JPanel(new BorderLayout(0,12)); hdrCard.setBackground(CARD);
        hdrCard.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,BDR),new EmptyBorder(22,28,18,28)));

        JPanel titleRow=new JPanel(new BorderLayout(16,0)); titleRow.setOpaque(false);
        JPanel hl=vBox();
        hl.add(lbl("Blood Requests",26,Font.BOLD,T1));
        hl.add(vgap(4));
        JPanel meta=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0)); meta.setOpaque(false);
        meta.add(statusPill("Requests — live view",BDR2,T3));
        hl.add(meta);
        titleRow.add(hl,BorderLayout.WEST);

        JPanel searchWrap=new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0)); searchWrap.setOpaque(false);
        JTextField search=lightField(260,38);
        search.putClientProperty("placeholder","Search requests...");
        JPanel sfRow=new JPanel(new BorderLayout(8,0)); sfRow.setOpaque(false);
        sfRow.add(lbl("🔍",14,Font.PLAIN,T3),BorderLayout.WEST);
        sfRow.add(search,BorderLayout.CENTER);
        searchWrap.add(sfRow);
        titleRow.add(searchWrap,BorderLayout.EAST);
        hdrCard.add(titleRow,BorderLayout.NORTH);

        JPanel pills=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); pills.setOpaque(false);
        pills.add(statusPill("Total: loading",BDR,T2));
        pills.add(statusPill("High Priority: red rows",CR_BD,CR));
        pills.add(statusPill("Approved: green rows",GR_BD,GR));
        hdrCard.add(pills,BorderLayout.SOUTH);

        String[] cols={"ID","Patient / Hospital","Phone","Blood","Units","Status","Priority","Date"};
        DefaultTableModel mdl=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable tbl=new JTable(mdl); styleTable(tbl);
        TableRowSorter<TableModel> sorter=new TableRowSorter<>(mdl); tbl.setRowSorter(sorter);
        search.addCaretListener(e->sorter.setRowFilter(RowFilter.regexFilter("(?i)"+search.getText())));

        int[] totalRows={0};
        try(Connection con=DBConnection.connect()){
            ResultSet rs=con.createStatement().executeQuery(
                "SELECT request_id,patient_name,phone,blood_group,units_requested,status,request_date,note FROM blood_requests ORDER BY request_id DESC");
            while(rs.next()){
                int sc=priority(rs.getString(4),rs.getString(8),rs.getTimestamp(7));
                mdl.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getObject(5),rs.getString(6),sc,fdate(rs.getTimestamp(7))});
                totalRows[0]++;
            }
        } catch(Exception ex){ex.printStackTrace();}
        ((JLabel)pills.getComponent(0)).setText("Total: "+totalRows[0]+" requests");

        tbl.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean f,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,f,r,c);
                setForeground(T1); setFont(new Font("SansSerif",Font.PLAIN,13)); setOpaque(true);
                if(!sel){
                    try{
                        int pri=(int)t.getValueAt(r,6); String st=(String)t.getValueAt(r,5);
                        if(pri>=60) setBackground(CR_BG);
                        else if(st.equalsIgnoreCase("Approved")) setBackground(GR_BG);
                        else setBackground(r%2==0?Color.WHITE:CARD2);
                    }catch(Exception e){setBackground(Color.WHITE);}
                } else setBackground(IN_BG);
                setBorder(new EmptyBorder(0,14,0,14)); return this;
            }
        });

        JPanel actionBar=new JPanel(new BorderLayout()); actionBar.setBackground(CARD);
        actionBar.setBorder(new CompoundBorder(new MatteBorder(1,0,0,0,BDR),new EmptyBorder(12,18,12,18)));
        JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btnRow.setOpaque(false);
        JButton vf=solidBtn("View File",IN), dl=solidBtn("Delete",SL), rj=solidBtn("Reject",CR), ap=solidBtn("✓  Approve",GR);

        vf.addActionListener(e->{
            int row=tbl.getSelectedRow(); if(row<0) return;
            try(Connection con=DBConnection.connect()){
                PreparedStatement ps=con.prepareStatement("SELECT requisition_file FROM blood_requests WHERE request_id=?");
                ps.setInt(1,(int)tbl.getValueAt(row,0)); ResultSet rs=ps.executeQuery();
                if(rs.next()&&rs.getString(1)!=null){File f=new File(rs.getString(1));if(f.exists())Desktop.getDesktop().open(f);else msg("File not found at path.");}
                else msg("No file attached to this request.");
            }catch(Exception ex){ex.printStackTrace();}
        });
        dl.addActionListener(e->{
            int row=tbl.getSelectedRow(); if(row<0) return;
            UIManager.put("OptionPane.background",Color.WHITE);UIManager.put("Panel.background",Color.WHITE);
                if(JOptionPane.showConfirmDialog(this,"Permanently delete this request?","Confirm Delete",JOptionPane.YES_NO_OPTION)==0){
                try(Connection con=DBConnection.connect()){
                    PreparedStatement ps=con.prepareStatement("DELETE FROM blood_requests WHERE request_id=?");
                    ps.setInt(1,(int)tbl.getValueAt(row,0)); ps.executeUpdate(); refresh();
                }catch(Exception ex){ex.printStackTrace();}
            }
        });
        ap.addActionListener(e->doStatus(tbl,"Approved"));
        rj.addActionListener(e->doStatus(tbl,"Rejected"));
        btnRow.add(vf); btnRow.add(dl); btnRow.add(rj); btnRow.add(ap);
        actionBar.add(lbl("Select a row to take action",11,Font.PLAIN,T3),BorderLayout.WEST);
        actionBar.add(btnRow,BorderLayout.EAST);

        JScrollPane sc=new JScrollPane(tbl); sc.setBorder(new MatteBorder(0,0,0,0,BDR));
        sc.setBackground(CARD); sc.getViewport().setBackground(CARD);

        JPanel tableArea=new JPanel(new BorderLayout()); tableArea.setBackground(CARD);
        tableArea.setBorder(new LineBorder(BDR,1));
        tableArea.add(sc,BorderLayout.CENTER); tableArea.add(actionBar,BorderLayout.SOUTH);

        JPanel inner=new JPanel(new BorderLayout(0,16)); inner.setOpaque(false);
        inner.add(hdrCard,BorderLayout.NORTH); inner.add(tableArea,BorderLayout.CENTER);
        p.add(inner,BorderLayout.CENTER); return p;
    }

    private JLabel statusPill(String text, Color bd, Color fg) {
        JLabel l=new JLabel("  "+text+"  ");
        l.setFont(new Font("SansSerif",Font.BOLD,10));
        l.setForeground(fg); l.setOpaque(true);
        l.setBackground(new Color(fg.getRed(),fg.getGreen(),fg.getBlue(),18));
        l.setBorder(new CompoundBorder(new LineBorder(bd,1),new EmptyBorder(2,6,2,6)));
        return l;
    }

    private void doStatus(JTable tbl, String status) {
        int row=tbl.getSelectedRow(); if(row<0) return;
        int mr=tbl.convertRowIndexToModel(row), id=(int)tbl.getModel().getValueAt(mr,0);
        Object units=tbl.getModel().getValueAt(mr,4);
        if(status.equals("Approved")&&(units==null||(int)units<=0)){
            UIManager.put("OptionPane.background",Color.WHITE);UIManager.put("Panel.background",Color.WHITE);
            String inp=JOptionPane.showInputDialog(this,"Enter units to issue:"); if(inp==null) return;
            try{units=Integer.parseInt(inp.trim());}catch(Exception e){return;}
        }
        try(Connection con=DBConnection.connect()){
            PreparedStatement ps=con.prepareStatement("UPDATE blood_requests SET status=?,units_requested=? WHERE request_id=?");
            ps.setString(1,status); ps.setObject(2,units); ps.setInt(3,id); ps.executeUpdate(); refresh();
        }catch(Exception e){e.printStackTrace();}
    }

    
    // STOCK
    
    private JPanel pgStock() {
        JPanel p=new JPanel(new BorderLayout(0,18)); p.setOpaque(false);
        JPanel hdr=new JPanel(new BorderLayout()); hdr.setOpaque(false);
        JPanel hl=vBox();
        hl.add(lbl("Blood Inventory",26,Font.BOLD,T1)); hl.add(vgap(3));
        hl.add(lbl("Live stock per group — auto-alerts fire for any group under 10 units",12,Font.PLAIN,T2));
        JButton ref=solidBtn("↻  Refresh",IN); ref.addActionListener(e->refresh());
        hdr.add(hl,BorderLayout.WEST); hdr.add(ref,BorderLayout.EAST);
        JPanel grid=new JPanel(new GridLayout(2,4,14,14)); grid.setOpaque(false);
        for(int i=0;i<8;i++) grid.add(stockCard(i));
        JPanel legend=new JPanel(new FlowLayout(FlowLayout.CENTER,20,4)); legend.setOpaque(false);
        legend.add(legChip("Critical  < 5 units",CR,CR_BG,CR_BD));
        legend.add(legChip("Low  5–9 units",AM,AM_BG,AM_BD));
        legend.add(legChip("Normal  10+ units",GR,GR_BG,GR_BD));
        p.add(hdr,BorderLayout.NORTH); p.add(grid,BorderLayout.CENTER); p.add(legend,BorderLayout.SOUTH);
        return p;
    }

    private JPanel stockCard(int i) {
        boolean crit=stock[i]<5, low=!crit&&stock[i]<10;
        Color ac=crit?CR:low?AM:GR, bgC=crit?CR_BG:low?AM_BG:GR_BG, bdC=crit?CR_BD:low?AM_BD:GR_BD;
        float pct=Math.min(1f,(float)stock[i]/60f);

        JPanel c=new JPanel(new BorderLayout()); c.setBackground(CARD);
        c.setBorder(new CompoundBorder(new MatteBorder(0,crit?4:2,0,0,ac),new EmptyBorder(16,16,16,16)));

        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(lbl(GRP[i],38,Font.BOLD,T1),BorderLayout.WEST);
        top.add(lbl(stock[i]+" units",13,Font.BOLD,ac),BorderLayout.EAST);

        JPanel bar=new JPanel(){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BDR); g2.fillRoundRect(0,0,getWidth(),8,8,8);
                int bw=(int)(getWidth()*pct); if(bw>0){g2.setColor(ac);g2.fillRoundRect(0,0,bw,8,8,8);}
            }
        };
        bar.setOpaque(false); bar.setPreferredSize(new Dimension(0,12));

        JLabel statusLbl=lbl(crit?"⚠  CRITICAL":low?"⚡  LOW STOCK":"✓  Normal",10,Font.BOLD,ac);

        String btnTxt=crit?"Re-send Alert":low?"Re-trigger":"Details";
        JButton actionBtn = new JButton(btnTxt) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter(){
                    public void mouseEntered(MouseEvent e){ hover=true; repaint(); }
                    public void mouseExited(MouseEvent e){ hover=false; repaint(); }
                });
            }
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg2 = hover ? CARD : bgC;
                g2.setColor(bg2); g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                g2.setColor(bdC); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
                g2.setColor(ac); g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()-fm.getHeight())/2+fm.getAscent());
            }
        };
        actionBtn.setFont(new Font("SansSerif",Font.BOLD,10));
        actionBtn.setOpaque(false); actionBtn.setContentAreaFilled(false); actionBtn.setBorderPainted(false);
        actionBtn.setFocusPainted(false); actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionBtn.setBorder(new EmptyBorder(5,12,5,12));
        actionBtn.addActionListener(e->{
            if(crit||low){autoAlert(); lightDialog("Dijkstra re-alert triggered for "+GRP[i]+" donors.\nSystem also runs every 5 min automatically.","Alert Sent",JOptionPane.INFORMATION_MESSAGE);}
            else lightDialog("Group: "+GRP[i]+"\nUnits: "+stock[i]+"\nStatus: Normal","Details",JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel botRow=new JPanel(new BorderLayout()); botRow.setOpaque(false);
        botRow.add(statusLbl,BorderLayout.WEST); botRow.add(actionBtn,BorderLayout.EAST);
        JPanel mid=new JPanel(); mid.setLayout(new BoxLayout(mid,BoxLayout.Y_AXIS)); mid.setOpaque(false);
        mid.add(vgap(8)); mid.add(bar); mid.add(vgap(8)); mid.add(botRow);
        c.add(top,BorderLayout.NORTH); c.add(mid,BorderLayout.CENTER); return c;
    }

    




    // DONORS
    
    private JPanel pgDonors() {
        JPanel p=new JPanel(new BorderLayout(0,16)); p.setOpaque(false);
        JPanel hl=vBox();
        hl.add(lbl("Donor Management",26,Font.BOLD,T1)); hl.add(vgap(3));
        hl.add(lbl("Leaderboard · Manual alerts · Recognition letters",12,Font.PLAIN,T2));
        p.add(hl,BorderLayout.NORTH);

        JPanel podium=new JPanel(new GridLayout(1,3,14,0)); podium.setOpaque(false); podium.setPreferredSize(new Dimension(0,130));
        try(Connection con=DBConnection.connect()){
            ResultSet rs=con.createStatement().executeQuery("SELECT name,points,donation_count,city FROM users WHERE role='User' ORDER BY points DESC LIMIT 3");
            String[] rk={"#1  Champion","#2  Elite","#3  Hero"};
            Color[] rc={new Color(202,138,4),SL,new Color(180,100,45)};
            Color[] rb={new Color(255,251,235),new Color(249,250,251),new Color(255,247,237)};
            int r=0; while(rs.next()&&r<3){podium.add(podCard(rk[r],rs.getString(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rc[r],rb[r]));r++;}
        }catch(Exception ignored){}

        String[] cols={"ID","Name","City","Blood Group","Points","Donations","Tier","Last Donation"};
        DefaultTableModel mdl=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable tbl=new JTable(mdl); styleTable(tbl);
        try(Connection con=DBConnection.connect()){
            ResultSet rs=con.createStatement().executeQuery("SELECT id,name,city,blood_group,points,donation_count,last_donation_date FROM users WHERE role='User' ORDER BY points DESC");
            while(rs.next()){
                int pts=rs.getInt(5);
                String tier=pts>=1000?"💎 Diamond":pts>=500?"🥇 Gold":pts>=100?"🥈 Silver":"🩸 Donor";
                mdl.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),pts,rs.getInt(6),tier,rs.getString(7)});
            }
        }catch(Exception ignored){}

        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btns.setOpaque(false);
        JButton ab=solidBtn("⚡  Alert This Donor",CR), lb=solidBtn("✉  Recognition Letter",GR);
        ab.addActionListener(e->{
            int row=tbl.getSelectedRow(); if(row<0){msg("Select a donor first.");return;}
            int mr=tbl.convertRowIndexToModel(row);
            int uid=(int)tbl.getModel().getValueAt(mr,0); String nm=(String)tbl.getModel().getValueAt(mr,1),bg=(String)tbl.getModel().getValueAt(mr,3);
            try(Connection con=DBConnection.connect()){
                PreparedStatement ps=con.prepareStatement("INSERT INTO notifications(user_id,message,is_read,created_at) VALUES(?,?,0,NOW())");
                ps.setInt(1,uid); ps.setString(2,"[ADMIN] URGENT: "+bg+" blood is critically needed. Please contact LifeLine Blood Bank immediately."); ps.executeUpdate();
            }catch(Exception ex){}
            lightDialog("Alert sent to "+nm+" ("+bg+")","✓ Sent",JOptionPane.INFORMATION_MESSAGE);
        });
        lb.addActionListener(e->{
            int row=tbl.getSelectedRow(); if(row<0){msg("Select a donor first.");return;}
            int mr=tbl.convertRowIndexToModel(row);
            doLetter((int)tbl.getModel().getValueAt(mr,0),(String)tbl.getModel().getValueAt(mr,1),(String)tbl.getModel().getValueAt(mr,6),(int)tbl.getModel().getValueAt(mr,4));
        });
        btns.add(ab); btns.add(lb);

        JScrollPane sc=new JScrollPane(tbl); sc.setBorder(new LineBorder(BDR,1)); sc.getViewport().setBackground(CARD); sc.setBackground(CARD);
        JPanel mid=new JPanel(new BorderLayout(0,12)); mid.setOpaque(false);
        mid.add(podium,BorderLayout.NORTH); mid.add(sc,BorderLayout.CENTER); mid.add(btns,BorderLayout.SOUTH);
        p.add(mid,BorderLayout.CENTER); return p;
    }

    private JPanel podCard(String rank,String name,int pts,int don,String city,Color ac,Color bgC) {
        JPanel c=new JPanel(new GridLayout(4,1,0,4)); c.setBackground(bgC);
        c.setBorder(new CompoundBorder(new MatteBorder(3,0,0,0,ac),new EmptyBorder(12,14,12,14)));
        c.add(lbl(rank,10,Font.BOLD,ac,SwingConstants.CENTER));
        c.add(lbl(name,14,Font.BOLD,T1,SwingConstants.CENTER));
        c.add(lbl(pts+" pts  ·  "+don+" donations",10,Font.PLAIN,T2,SwingConstants.CENTER));
        c.add(lbl(city!=null?city:"—",10,Font.PLAIN,T3,SwingConstants.CENTER));
        return c;
    }

    private void doLetter(int uid,String name,String tier,int pts){
        String date=LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        int lives=Math.max(3,(pts/100)*3);
        String body="══════════════════════════════════════════\n   LIFELINE BLOOD BANK\n   CERTIFICATE OF RECOGNITION\n══════════════════════════════════════════\n\n"+
            "Dear "+name+",\n\nWe proudly recognise your achievement of\n"+tier.toUpperCase()+" status.\n\n"+
            "Your "+pts+" points represent approximately\n"+lives+" lives saved through your generosity.\n\n"+
            "  \"Every drop you give is a life you save.\"\n\n"+
            "                        Date: "+date+"\n══════════════════════════════════════════";
        try(Connection con=DBConnection.connect()){
            PreparedStatement ps=con.prepareStatement("INSERT INTO notifications(user_id,message,is_read,created_at) VALUES(?,?,0,NOW())");
            ps.setInt(1,uid); ps.setString(2,"LifeLine: "+tier+" Recognition Award! Thank you."); ps.executeUpdate();
        }catch(Exception ignored){}
        JTextArea area=new JTextArea(body);
        area.setFont(new Font("Monospaced",Font.PLAIN,12));
        area.setBackground(new Color(255,253,240)); area.setForeground(T1);
        area.setEditable(false); area.setMargin(new Insets(14,14,14,14));
        JScrollPane sp=new JScrollPane(area); sp.setPreferredSize(new Dimension(460,300));
        lightDialog(sp,"Certificate — "+name,JOptionPane.INFORMATION_MESSAGE);
    }

    


    // ALERTS
    
    private JPanel pgAlerts() {
        JPanel p=new JPanel(new BorderLayout(0,16)); p.setOpaque(false);
        JPanel hl=vBox();
        hl.add(lbl("Emergency Alert System",26,Font.BOLD,T1)); hl.add(vgap(3));
        hl.add(lbl("Dijkstra auto-routing  ·  Every 5 min  ·  Manual override below",12,Font.PLAIN,T2));
        p.add(hl,BorderLayout.NORTH);

        JPanel ic=new JPanel(new BorderLayout(0,10)); ic.setBackground(IN_BG);
        ic.setBorder(new CompoundBorder(new LineBorder(IN_BD,1),new EmptyBorder(16,20,16,20)));
        ic.add(lbl("HOW THE DIJKSTRA AUTO-ALERT ENGINE WORKS",11,Font.BOLD,IN),BorderLayout.NORTH);
        JTextArea infoTxt=new JTextArea(
            "Every 5 minutes (and on admin login), the system runs automatically:\n"+
            "  1. Scans blood_stock_summary for any group with units < 10\n"+
            "  2. Runs Dijkstra shortest-path from Kathmandu hub across Nepal road network\n"+
            "  3. Queries users + donors tables — filters by blood_group + city\n"+
            "  4. Ranks all matching donors by road distance (nearest first)\n"+
            "  5. Sends notification to top 3 nearest donors (skips if alerted within 24h)\n"+
            "  6. Logs all actions to system_alerts for full audit trail\n\n"+
            "EXAMPLE:  B− stock = 4 units (CRITICAL)\n"+
            "  Hari  (B−, Palpa)     =  45 km  →  NOTIFIED #1  (auto)\n"+
            "  Sita  (B−, Pokhara)   = 200 km  →  NOTIFIED #2  (auto)\n"+
            "  Ram   (B−, Kathmandu) =   0 km  →  NOTIFIED #3  (auto)"
        );
        infoTxt.setFont(new Font("Monospaced",Font.PLAIN,11)); infoTxt.setOpaque(false);
        infoTxt.setForeground(IN); infoTxt.setEditable(false);
        ic.add(infoTxt,BorderLayout.CENTER);

        JPanel fc=card(); fc.setLayout(new BorderLayout(0,16));

        JPanel fcHead=new JPanel(new BorderLayout(0,3)); fcHead.setOpaque(false);
        fcHead.add(lbl("Manual Override — Targeted Alert",14,Font.BOLD,T1),BorderLayout.NORTH);
        fcHead.add(lbl("Fill in the details below and dispatch an immediate targeted alert",11,Font.PLAIN,T3),BorderLayout.SOUTH);
        fc.add(fcHead,BorderLayout.NORTH);

        JSeparator sep=new JSeparator(); sep.setForeground(BDR); sep.setBackground(BDR);

        JTextField pf=lightField(0,38), hf=lightField(0,38), uf=lightField(0,38);
        uf.setText("1");
        JComboBox<String> lc=lightCombo(GRAPH.keySet().toArray(new String[0]));
        JComboBox<String> gc=lightCombo(new String[]{"A+","A-","B+","B-","O+","O-","AB+","AB-"});

        JPanel row1=new JPanel(new GridLayout(1,3,16,0)); row1.setOpaque(false);
        row1.add(fldGroup("Patient Name",pf));
        row1.add(fldGroup("Hospital Name",hf));
        row1.add(fldGroup("City / Location",lc));

        JPanel row2=new JPanel(new GridLayout(1,3,16,0)); row2.setOpaque(false);
        row2.add(fldGroup("Blood Group",gc));
        row2.add(fldGroup("Units Needed",uf));
        row2.add(new JPanel(){{setOpaque(false);}});

        JButton sendBtn=solidBtn("⚡  Dispatch Dijkstra Alert",CR);
        sendBtn.setFont(new Font("SansSerif",Font.BOLD,13));
        JPanel bw=new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0)); bw.setOpaque(false); bw.add(sendBtn);

        JPanel formGrid=new JPanel(new GridLayout(2,1,0,14)); formGrid.setOpaque(false);
        formGrid.add(row1); formGrid.add(row2);

        JTextArea out=new JTextArea(8,60);
        out.setFont(new Font("Monospaced",Font.PLAIN,11));
        out.setBackground(new Color(248,249,251)); out.setForeground(T2);
        out.setOpaque(true); out.setEditable(false);
        out.setBorder(new EmptyBorder(12,14,12,14));
        out.setText("Dijkstra routing output will appear here after dispatching an alert...");
        JScrollPane os=new JScrollPane(out);
        os.setBorder(new LineBorder(BDR,1));
        os.setBackground(new Color(248,249,251)); os.getViewport().setBackground(new Color(248,249,251));

        sendBtn.addActionListener(e->{
            String pt=pf.getText().trim(),hn=hf.getText().trim();
            String loc=(String)lc.getSelectedItem(),grp=(String)gc.getSelectedItem();
            int u=1; try{u=Integer.parseInt(uf.getText().trim());}catch(Exception ignored){}
            if(pt.isEmpty()||hn.isEmpty()){lightDialog("Fill in Patient Name and Hospital Name.","Required",JOptionPane.WARNING_MESSAGE);return;}
            doManualAlert(pt,hn,loc,grp,u,out);
        });

        JPanel formSection=new JPanel(new BorderLayout(0,10)); formSection.setOpaque(false);
        formSection.add(sep,BorderLayout.NORTH); formSection.add(formGrid,BorderLayout.CENTER); formSection.add(bw,BorderLayout.SOUTH);
        JPanel outSec=new JPanel(new BorderLayout(0,6)); outSec.setOpaque(false);
        outSec.add(lbl("Dijkstra Routing Output:",11,Font.BOLD,T2),BorderLayout.NORTH); outSec.add(os,BorderLayout.CENTER);

        JPanel body=new JPanel(new BorderLayout(0,14)); body.setOpaque(false);
        body.add(formSection,BorderLayout.NORTH); body.add(outSec,BorderLayout.CENTER);
        fc.add(body,BorderLayout.CENTER);

        JPanel main=new JPanel(new BorderLayout(0,14)); main.setOpaque(false);
        main.add(ic,BorderLayout.NORTH); main.add(fc,BorderLayout.CENTER);
        p.add(main,BorderLayout.CENTER); return p;
    }







    //  Donor count number
    private void doManualAlert(String patient,String hospital,String loc,String grp,int units,JTextArea out){
        StringBuilder sb=new StringBuilder();
        sb.append("══════════════════════════════════════════════════\n");
        sb.append("  LIFELINE  ·  MANUAL DIJKSTRA ALERT\n");
        sb.append("══════════════════════════════════════════════════\n");
        sb.append(String.format("  Patient  : %s%n  Hospital : %s%n  City     : %s%n  Group    : %s%n  Units    : %d%n%n",patient,hospital,loc,grp,units));
        Map<String,Integer> dists=dijkstra(loc);
        sb.append("  ► Dijkstra from: "+loc+" → "+dists.size()+" cities computed\n\n");
        List<DR> donors=new ArrayList<>();
        try(Connection con=DBConnection.connect()){
            PreparedStatement ps=con.prepareStatement("SELECT id,name,city FROM users WHERE blood_group=? AND role='User' AND city IS NOT NULL AND city!=''");
            ps.setString(1,grp); ResultSet rr=ps.executeQuery();
            while(rr.next()) donors.add(new DR(rr.getInt(1),rr.getString(2),rr.getString(3),resDist(dists,rr.getString(3)),"users"));
            PreparedStatement ps2=con.prepareStatement("SELECT id,name,city FROM donors WHERE blood_group=? AND city IS NOT NULL AND city!=''");
            ps2.setString(1,grp); ResultSet rr2=ps2.executeQuery();
            while(rr2.next()) donors.add(new DR(rr2.getInt(1),rr2.getString(2),rr2.getString(3),resDist(dists,rr2.getString(3)),"donors"));
        }catch(Exception ex){
            donors.add(new DR(6,"Suman Hamal","Butwal",dists.getOrDefault("Butwal",9999),"demo"));
            donors.add(new DR(7,"Anjali Thapa","Kathmandu",dists.getOrDefault("Kathmandu",9999),"demo"));
            donors.add(new DR(8,"Bipul Chettri","Pokhara",dists.getOrDefault("Pokhara",9999),"demo"));
            sb.append("  (Demo mode — sample donors shown)\n");
        }
        if(donors.isEmpty()){sb.append("  ✗ No donors found for "+grp);out.setText(sb.toString());return;}
        donors.sort(Comparator.comparingInt(d->d.dist));
        sb.append(String.format("  Found %d donor(s) — ranked by road distance:%n%n",donors.size()));
        sb.append(String.format("  %-4s %-22s %-14s%n","#","Name","Distance"));
        sb.append("  ──────────────────────────────────────────────────\n");
        int rank=1; List<DR> toSend=new ArrayList<>();
        for(DR d:donors){
            String ds=d.dist==Integer.MAX_VALUE?"Unknown":d.dist+" km";
            sb.append(String.format("  %-4d %-22s %-14s%s%n",rank,d.name,ds,rank<=3?"  ◀ NOTIFIED":""));
            if(rank<=3) toSend.add(d); rank++;
        }
        int sent=0;
        try(Connection con=DBConnection.connect()){
            PreparedStatement ins=con.prepareStatement("INSERT INTO notifications(user_id,message,is_read,created_at) VALUES(?,?,0,NOW())");
            for(DR d:toSend){
                if(!"users".equals(d.src)&&!"demo".equals(d.src)) continue;
                ins.setInt(1,d.uid);
                ins.setString(2,"[MANUAL] URGENT: "+patient+" at "+hospital+" needs "+units+" unit(s) of "+grp+". You are "+(d.dist==Integer.MAX_VALUE?"nearby":d.dist+"km away")+". Please respond immediately!");
                ins.executeUpdate(); sent++;
            }
            PreparedStatement log=con.prepareStatement("INSERT INTO system_alerts(blood_group,message,created_at) VALUES(?,?,NOW())");
            log.setString(1,grp); log.setString(2,"Manual: "+patient+" @ "+hospital+" → "+sent+" "+grp+" donors notified."); log.executeUpdate();
        }catch(Exception ex){sent=toSend.size(); sb.append("\n  (Demo: notifications logged)\n");}
        sb.append(String.format("%n  ► Dispatched to %d nearest donor(s).%n",sent));
        sb.append("══════════════════════════════════════════════════\n");
        out.setText(sb.toString()); alertsCnt+=sent;
        
        
        int totalFound = donors.size();
        lightDialog("Found "+totalFound+" matching "+grp+" donor(s).\n\nSuccessfully dispatched alerts to "+sent+" nearest donor(s) near "+loc+"!","✓ Dispatched",JOptionPane.INFORMATION_MESSAGE);
    }

    
    
    private JPanel pgAnalytics() {
        JPanel p=new JPanel(new BorderLayout(0,16)); p.setOpaque(false);
        JPanel hl=vBox();
        hl.add(lbl("Analytics & Insights",26,Font.BOLD,T1)); hl.add(vgap(3));
        hl.add(lbl("System health · Stock breakdown · Notification history",12,Font.PLAIN,T2));

        int total=0; for(int v:stock) total+=v;
        JPanel stats=new JPanel(new GridLayout(1,4,14,0)); stats.setOpaque(false);
        stats.add(aCard("Total Blood Units",total+"",IN,IN_BG,IN_BD));
        stats.add(aCard("Low Stock Groups",lowCnt+"",CR,CR_BG,CR_BD));
        stats.add(aCard("Pending Requests",pendCnt+"",AM,AM_BG,AM_BD));
        stats.add(aCard("Alerts Today",alertsCnt+"",VT,VT_BG,VT_BD));

        String[] c1={"Blood Group","Units Available","Stock Status","Alert Status"};
        DefaultTableModel m1=new DefaultTableModel(c1,0){public boolean isCellEditable(int r,int c){return false;}};
        for(int i=0;i<8;i++){
            String st=stock[i]<5?"CRITICAL":stock[i]<10?"LOW":"Normal";
            m1.addRow(new Object[]{GRP[i],stock[i]+" units",st,stock[i]<10?"Auto-alerted ✓":"No action needed"});
        }
        JTable t1=new JTable(m1); styleTable(t1);
        t1.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                super.getTableCellRendererComponent(t,v,s,f,r,c);
                setForeground(T1); setFont(new Font("SansSerif",Font.BOLD,11)); setOpaque(true);
                String val=v!=null?v.toString():"";
                setBackground(s?IN_BG:val.equals("CRITICAL")?CR_BG:val.equals("LOW")?AM_BG:GR_BG);
                setBorder(new EmptyBorder(0,12,0,12)); return this;
            }
        });

        String[] c2={"ID","Recipient","Message","Read","Sent At"};
        DefaultTableModel m2=new DefaultTableModel(c2,0){public boolean isCellEditable(int r,int c){return false;}};
        try(Connection con=DBConnection.connect()){
            ResultSet rs=con.createStatement().executeQuery(
                "SELECT n.id,u.name,n.message,n.is_read,n.created_at FROM notifications n LEFT JOIN users u ON n.user_id=u.id ORDER BY n.created_at DESC LIMIT 30");
            while(rs.next()) m2.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4)==0?"Unread":"Read",fdate(rs.getTimestamp(5))});
        }catch(Exception ignored){}
        JTable t2=new JTable(m2); styleTable(t2);
        t2.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                super.getTableCellRendererComponent(t,v,s,f,r,c);
                setFont(new Font("SansSerif",Font.BOLD,10)); boolean ur="Unread".equals(v); setOpaque(true);
                setForeground(ur?CR:GR); setBackground(s?IN_BG:ur?CR_BG:GR_BG);
                setBorder(new EmptyBorder(0,12,0,12)); return this;
            }
        });

        JScrollPane sp1=new JScrollPane(t1); sp1.setBorder(null); sp1.setBackground(CARD); sp1.getViewport().setBackground(CARD);
        JScrollPane sp2=new JScrollPane(t2); sp2.setBorder(null); sp2.setBackground(CARD); sp2.getViewport().setBackground(CARD);
        JTabbedPane tabs=new JTabbedPane(); tabs.setFont(new Font("SansSerif",Font.BOLD,12)); tabs.setBackground(CARD);
        tabs.addTab("📊  Stock Breakdown",sp1); tabs.addTab("🔔  Notification Log (last 30)",sp2);

        JPanel main=new JPanel(new BorderLayout(0,14)); main.setOpaque(false);
        main.add(stats,BorderLayout.NORTH); main.add(tabs,BorderLayout.CENTER);
        p.add(hl,BorderLayout.NORTH); p.add(main,BorderLayout.CENTER); return p;
    }

    private JPanel aCard(String label,String val,Color ac,Color bgC,Color bdC){
        JPanel c=new JPanel(new GridLayout(2,1,0,5)); c.setBackground(bgC);
        c.setBorder(new CompoundBorder(new LineBorder(bdC,1),new EmptyBorder(14,16,14,16)));
        c.add(lbl(val,30,Font.BOLD,ac,SwingConstants.CENTER));
        c.add(lbl(label,11,Font.PLAIN,T2,SwingConstants.CENTER));
        return c;
    }




    
    // COMPONENT FACTORIES
    

    private JTextField lightField(int w, int h) {
        JTextField f=new JTextField();
        if(w>0) f.setPreferredSize(new Dimension(w,h));
        else    f.setPreferredSize(new Dimension(0,h));
        f.setFont(new Font("SansSerif",Font.PLAIN,13));
        f.setBackground(Color.WHITE); f.setForeground(T1);
        f.setCaretColor(T1); f.setOpaque(true);
        f.setBorder(new CompoundBorder(new LineBorder(BDR2,1,true),new EmptyBorder(6,12,6,12)));
        return f;
    }

    @SuppressWarnings("unchecked")
    private JComboBox<String> lightCombo(Object[] items) {
        JComboBox<String> c=new JComboBox<>();
        for(Object o:items) c.addItem(o.toString());
        c.setFont(new Font("SansSerif",Font.PLAIN,13));
        c.setBackground(Color.WHITE); c.setForeground(T1);
        c.setOpaque(true); c.setPreferredSize(new Dimension(0,38));
        c.setBorder(new LineBorder(BDR2,1));
        c.setRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList<?> list,Object value,int idx,boolean sel,boolean foc){
                super.getListCellRendererComponent(list,value,idx,sel,foc);
                setBackground(sel?IN_BG:Color.WHITE); setForeground(T1);
                setFont(new Font("SansSerif",Font.PLAIN,13));
                setBorder(new EmptyBorder(5,12,5,12)); setOpaque(true); return this;
            }
        });
        return c;
    }

    private JPanel fldGroup(String labelText, Component input) {
        JPanel g=new JPanel(new BorderLayout(0,5)); g.setOpaque(false);
        JLabel l=new JLabel(labelText);
        l.setFont(new Font("SansSerif",Font.BOLD,11)); l.setForeground(T2);
        g.add(l,BorderLayout.NORTH); g.add(input,BorderLayout.CENTER);
        return g;
    }

    private JButton solidBtn(String text, Color bg) {
        Color dk = bg.darker();
        JButton b = new JButton(text) {
            private Color cur = bg;
            {
                addMouseListener(new MouseAdapter(){
                    public void mouseEntered(MouseEvent e){ cur=dk; repaint(); }
                    public void mouseExited(MouseEvent e){ cur=bg; repaint(); }
                });
            }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cur); g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()-fm.stringWidth(getText()))/2;
                int ty = (getHeight()-fm.getHeight())/2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
            }
        };
        b.setFont(new Font("SansSerif",Font.BOLD,12));
        b.setForeground(Color.WHITE); b.setBackground(bg);
        b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(9,20,9,20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton plainBtn(String text, Color fg) {
        JButton b = new JButton(text) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter(){
                    public void mouseEntered(MouseEvent e){ hover=true; repaint(); }
                    public void mouseExited(MouseEvent e){ hover=false; repaint(); }
                });
            }
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? CR_BG : CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                g2.setColor(hover ? CR_BD : BDR); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
                g2.setColor(hover ? CR : fg); g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()-fm.getHeight())/2+fm.getAscent());
            }
        };
        b.setFont(new Font("SansSerif",Font.PLAIN,12));
        b.setForeground(fg); b.setBackground(CARD);
        b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(8,14,8,14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setMaximumSize(new Dimension(9999,36));
        return b;
    }

    private JPanel card() {
        JPanel c=new JPanel(); c.setBackground(CARD);
        c.setBorder(new CompoundBorder(new LineBorder(BDR,1),new EmptyBorder(20,22,20,22)));
        return c;
    }
    private JPanel vBox(){JPanel p=new JPanel();p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));p.setOpaque(false);return p;}
    private Component vgap(int h){return Box.createVerticalStrut(h);}
    private void msg(String m) { lightDialog(m, "Info", JOptionPane.INFORMATION_MESSAGE); }
    private void lightDialog(Object m, String title, int type) {
        UIManager.put("OptionPane.background",        Color.WHITE);
        UIManager.put("OptionPane.messageForeground", T1);
        UIManager.put("Panel.background",             Color.WHITE);
        UIManager.put("Button.background",            Color.WHITE);
        UIManager.put("Button.foreground",            T1);
        JOptionPane.showMessageDialog(this, m, title, type);
        UIManager.put("Panel.background",             PAGE_BG);
    }

    private JPanel legChip(String t, Color ac, Color bgC, Color bdC){
        JPanel ch=new JPanel(new FlowLayout(FlowLayout.LEFT,5,2)); ch.setBackground(bgC);
        ch.setBorder(new CompoundBorder(new LineBorder(bdC,1),new EmptyBorder(3,10,3,10)));
        ch.add(lbl(t,11,Font.BOLD,ac)); return ch;
    }

    private JLabel lbl(String t,int sz,int style,Color c){
        JLabel l=new JLabel(t); l.setFont(new Font("SansSerif",style,sz)); l.setForeground(c); return l;
    }
    private JLabel lbl(String t,int sz,int style,Color c,int a){
        JLabel l=new JLabel(t,a); l.setFont(new Font("SansSerif",style,sz)); l.setForeground(c); return l;
    }

    private void styleTable(JTable t) {
        t.setRowHeight(42); t.setFont(new Font("SansSerif",Font.PLAIN,13));
        t.setBackground(Color.WHITE); t.setForeground(T1);
        t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0,0));
        t.setSelectionBackground(IN_BG); t.setSelectionForeground(T1);
        t.setFillsViewportHeight(true); t.setOpaque(true);

        Color HDR=new Color(248,249,251);
        JTableHeader th=t.getTableHeader();
        th.setBackground(HDR); th.setForeground(T2); th.setOpaque(true);
        th.setFont(new Font("SansSerif",Font.BOLD,11));
        th.setBorder(new MatteBorder(0,0,2,0,BDR));
        th.setReorderingAllowed(false);
        th.setDefaultRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable ta,Object v,boolean sel,boolean foc,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(ta,v,sel,foc,r,c);
                l.setBackground(HDR); l.setForeground(T2); l.setOpaque(true);
                l.setFont(new Font("SansSerif",Font.BOLD,11));
                l.setBorder(new CompoundBorder(new MatteBorder(0,0,0,1,BDR),new EmptyBorder(0,14,0,14)));
                return l;
            }
        });

        t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable ta,Object v,boolean s,boolean f,int r,int c){
                super.getTableCellRendererComponent(ta,v,s,f,r,c);
                setFont(new Font("SansSerif",Font.PLAIN,13)); setForeground(T1); setOpaque(true);
                setBackground(s?IN_BG:(r%2==0?Color.WHITE:CARD2));
                setBorder(new EmptyBorder(0,14,0,14)); return this;
            }
        });
    }

    private String clk(){return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss  ·  EEE d MMM"));}
    private String dayStr(){return LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));}
    private String greeting(){int h=LocalDateTime.now().getHour();return h<12?"morning":h<17?"afternoon":"evening";}
    private String fdate(Timestamp ts){return ts==null?"—":ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("MMM d, yyyy"));}

   
   
   
   
   
   
    // STOCK BAR CHART 
    class StockChart extends JPanel {
        StockChart(){setOpaque(true); setBackground(CARD); setPreferredSize(new Dimension(460,200));}
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight(),pL=40,pB=30,pT=14,ch=h-pB-pT,maxU=60;
            int bw=Math.max(22,(w-pL-16)/8-10);
            for(int i=0;i<=4;i++){
                int y=h-pB-(int)(i*ch/4.0);
                g2.setColor(BDR); g2.drawLine(pL,y,w-6,y);
                g2.setFont(new Font("SansSerif",Font.PLAIN,9)); g2.setColor(T3);
                g2.drawString(i*(maxU/4)+"",pL-26,y+4);
            }
            for(int i=0;i<8;i++){
                int val=stock[i],bh=(int)(Math.min(val,maxU)/(double)maxU*ch);
                int x=pL+8+i*(bw+10),y=h-pB-bh;
                Color base=val<5?CR:val<10?AM:GR;
                Color fade=new Color(base.getRed(),base.getGreen(),base.getBlue(),30);
                if(bh>0){
                    java.awt.GradientPaint gp=new java.awt.GradientPaint(x,y,base,x,h-pB,fade);
                    g2.setPaint(gp); g2.fillRoundRect(x,y,bw,bh,6,6);
                    g2.setColor(T1); g2.setFont(new Font("SansSerif",Font.BOLD,9));
                    String vt=val+""; int tw=g2.getFontMetrics().stringWidth(vt);
                    g2.drawString(vt,x+(bw-tw)/2,Math.max(y-4,pT+8));
                }
                g2.setColor(T2); g2.setFont(new Font("SansSerif",Font.BOLD,9));
                String gt=GRP[i]; int gw=g2.getFontMetrics().stringWidth(gt);
                g2.drawString(gt,x+(bw-gw)/2,h-pB+16);
            }
        }
    }

    // DATA CLASSES 
    static class DR {
        int uid,dist; String name,city,src;
        DR(int u,String n,String c,int d,String s){uid=u;name=n;city=c;dist=d;src=s;}
    }
    static class TxRow {
        String name,bg,status; int units; Timestamp date;
        TxRow(String n,String b,int u,String s,Timestamp d){name=n;bg=b;units=u;status=s;date=d;}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDash().setVisible(true));
    }
}