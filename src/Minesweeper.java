import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Minesweeper {
    static private int midtime = 3600,mineNum = 0;
    private static ImageIcon icon = new ImageIcon("icon.png");
    static private JLabel label1,label2;
    static private GamePanel gp;

    Minesweeper(){
        JFrame f = new JFrame("minesweeper");
        f.setBounds(600,200,500,600);
        f.setDefaultCloseOperation(3);
        f.setLayout(null);
        label1 = new JLabel("time：" +(midtime / 60 / 60 % 60) + ":"+ (midtime / 60 % 60)+ ":" +(midtime % 60));
        label1.setBounds(10,20,120,20);
        f.add(label1);

        label2 = new JLabel("undo:"+mineNum);
        label2.setBounds(400,20,120,20);
        f.add(label2);

        JButton bt = new JButton(icon);
        bt.setBounds(230, 15,30,30);
        bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                f.dispose();
                midtime = 3600;
                new Minesweeper();
            }
        });
        f.add(bt);

        gp = new GamePanel(20,20);
        gp.setBounds(40,100,400,400);
        f.add(gp);

        f.setVisible(true);
    }



    static class CountDown extends Thread{
        public void run(){
            while (midtime > 0){
                try{
                    -- midtime;
                    label1.setText("Time：" +(midtime / 60 / 60 % 60) + ":"+ (midtime / 60 % 60)+ ":" +(midtime % 60));
                    this.sleep(1000);
                }catch (Exception e){
                    System.out.println("error：" + e.toString());
                }
            }
            if(midtime == 0) {
                gp.showBomb();
                JOptionPane.showMessageDialog(null,"time is up","Over",JOptionPane.PLAIN_MESSAGE);
            }
        }

    }
    public static void main(String[] args){
        new Minesweeper();

        CountDown cd = new CountDown();
        cd.start();
    }


    public static void setMineNum(int i){
        mineNum = i;
        label2.setText("undo:"+mineNum);
    }
}

class GamePanel extends JPanel {
    private int rows, cols, bombCount,flagNum;
    private final int BLOCKWIDTH = 20;
    private final int BLOCKHEIGHT = 20;
    private JLabel[][] label;
    private boolean[][] state;
    private Btn[][] btns;
    private byte[][] click;
    private static ImageIcon flag = new ImageIcon("flag.jpg");
    private static ImageIcon bomb = new ImageIcon("bomb.jpg");
    private static ImageIcon lucency = new ImageIcon("lucency.png");


    public GamePanel(int row, int col) {
        rows = row;
        cols = col;
        bombCount = rows * cols / 10;
        flagNum = bombCount;
        label = new JLabel[rows][cols];
        state = new boolean[rows][cols];
        btns = new Btn[rows][cols];
        click = new byte[rows][cols];

        Minesweeper.setMineNum(flagNum);
        setLayout(null);
        initLable();
        randomBomb();
        writeNumber();
        randomBtn();
    }

    public void initLable() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                JLabel l = new JLabel("", JLabel.CENTER);
                l.setBounds(j * BLOCKWIDTH, i * BLOCKHEIGHT, BLOCKWIDTH, BLOCKHEIGHT);
                l.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                l.setOpaque(true);
                l.setBackground(Color.lightGray);

                this.add(l);

                label[i][j] = l;
                label[i][j].setVisible(false);
            }
        }
    }



    private void randomBomb() {
        for (int i = 0; i < bombCount; i++) {
            int rRow = (int) (Math.random() * rows);
            int rCol = (int) (Math.random() * cols);
            label[rRow][rCol].setIcon(bomb);
            if (state[rRow][rCol]==true){
                bombCount+=1; continue;
            }
            state[rRow][rCol] = true;
        }
    }


    private void writeNumber() {
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (state[i][j]) {
                    continue;
                }
                int bombCount = 0;

                for (int r = -1; (r + i < rows) && (r < 2); ++r) {
                    if (r + i < 0) continue;
                    for (int c = -1; (c + j < cols) && (c < 2); ++c) {
                        if (c + j < 0) continue;
                        if (state[r + i][c + j]) ++bombCount;
                    }
                }
                if (bombCount > 0) {
                    click[i][j] = 2;
                    label[i][j].setText(String.valueOf(bombCount));
                }
            }
        }
    }


    private void randomBtn() {
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                Btn btn = new Btn();
                btn.i = i;
                btn.j = j;
                btn.setBounds(j * BLOCKWIDTH, i * BLOCKHEIGHT, BLOCKWIDTH, BLOCKHEIGHT);
                this.add(btn);
                btns[i][j] = btn;
                btn.addMouseListener(new MouseAdapter() {
                                         public void mouseClicked(MouseEvent e) {

                                             if(e.getButton() == MouseEvent.BUTTON1) open(btn);

                                             if(e.getButton() == MouseEvent.BUTTON3) placeFlag(btn);
                                         }

                                     }
                );

            }
        }

    }

    private void open(Btn b){
        if(state[b.i][b.j]){
            for (int r = 0;r < rows;++r){
                for(int c = 0;c < cols; ++c){
                    btns[r][c].setVisible(false);/* 隐藏label */
                    label[r][c].setVisible(true);/* 显示按钮（这里只有隐藏了按钮才能显示按钮下面的label） */
                }
            }
            JOptionPane.showMessageDialog(null,"you lose","Over",JOptionPane.PLAIN_MESSAGE);
        }else{
            subopen(b);
        }
    }


    private void subopen(Btn b){

        if(state[b.i][b.j]) return;

        if(click[b.i][b.j] == 1 || click[b.i][b.j] == 4) return;

        if(click[b.i][b.j] == 2) {
            b.setVisible(false);
            label[b.i][b.j].setVisible(true);
            click[b.i][b.j] = 1;
            return;
        }

        b.setVisible(false);
        label[b.i][b.j].setVisible(true);
        click[b.i][b.j] = 1;

        for (int r = -1; (r + b.i < rows) && (r < 2); ++r) {
            if (r + b.i < 0) continue;
            for (int c = -1; (c + b.j < cols) && (c < 2); ++c) {
                if (c + b.j < 0) continue;
                if (r==0 && c==0) continue;
                Btn newbtn = btns[r + b.i][c + b.j];
                subopen(newbtn);
            }
        }
    }


    private void placeFlag(Btn b){
        if(flagNum>0){
            if(click[b.i][b.j] == 3){
                if(label[b.i][b.j].getText() == "[0-9]") click[b.i][b.j] = 2;
                else click[b.i][b.j] = 0;
                b.setIcon(lucency);
                ++ flagNum;
                Minesweeper.setMineNum(flagNum);
            }else {
                b.setIcon(flag);
                click[b.i][b.j] = 3;
                -- flagNum;
                Minesweeper.setMineNum(flagNum);
            }

            if(flagNum == 0){
                boolean flagstate = true;
                for(int i = 0;i < rows; ++i){
                    for(int j = 0;j < cols; ++j){
                        if (click[i][j] != 3 && state[i][j]) flagstate = false;
                    }
                }
                if(flagstate) JOptionPane.showMessageDialog(null,"you win","over",JOptionPane.PLAIN_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"you lose","the flags have been used up",JOptionPane.PLAIN_MESSAGE);
        }
    }

    public void showBomb(){
        for (int r = 0;r < rows;++r){
            for(int c = 0;c < cols; ++c){
                btns[r][c].setVisible(false);
                label[r][c].setVisible(true);
            }
        }
    }
}

class Btn extends JButton{
    public int i,j;}
