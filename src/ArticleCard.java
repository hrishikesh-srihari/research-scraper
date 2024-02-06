import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class ArticleCard extends JFrame {
    private JTextPane EverythButAbs;
    private JPanel panel1;
    private JTextPane Abstract;
    private JButton nextArticleButton;
    private JButton previousArticleButton;
    private JButton exitButton;
    private JComboBox SortBy;
    private JScrollPane InfoScrollPane;
    private JScrollPane AbstractScrollPane;
    private JButton deleteArticleButton;
    private JCheckBox lightModeCheckBox;
    private JLabel OverviewLabel;
    private JLabel AbstractLabel;
    private JButton insertArticleButton;
    private JButton exportButton;
    private JPanel DeleteInsertPanel;
    private JPanel LeavePanel;
    static ATDSProject project = new ATDSProject();

    DLL articleList;

    DLL.Node currNextButton;

    public ArticleCard() throws FileNotFoundException {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setContentPane(panel1);
        InfoScrollPane.getVerticalScrollBar().setBackground(Color.decode("#9cc5a1"));
        AbstractScrollPane.getVerticalScrollBar().setBackground(Color.decode("#9cc5a1"));
        setTitle("Research Article Tool");
        setSize(screenSize.width, screenSize.height);
        project.setupScrape();
        articleList = project.getDataAndStoreInDB();
        currNextButton = articleList.head;
        DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
        dlcr.setBackground(Color.decode("#FFFFFF"));
        dlcr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
        SortBy.setRenderer(dlcr);

        try {
            ResearchArticle first = articleList.head.data;
            Abstract.setText("\t" +  currNextButton.data.getAbstract());
            EverythButAbs.setText("TITLE: " + currNextButton.data.getTitle() + "\n\n" + "AUTHOR: " + currNextButton.data.getAuthor() + "\n\n" + "PUBLICATION DATE: " + currNextButton.data.getPubDate() + "\n\n" + "TOTAL ARTICLE VIEWS: " + currNextButton.data.getViews());
            setVisible(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        catch (NoSuchElementException e) {
            JOptionPane.showMessageDialog(null, "There are no articles to display.",
                    "Invalid", JOptionPane.ERROR_MESSAGE);
        }


        nextArticleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currNextButton.next != null) {
                    currNextButton = currNextButton.next;
                    ResearchArticle next = currNextButton.next.data;
                    Abstract.setText("\t" + currNextButton.data.getAbstract());
                    EverythButAbs.setText("TITLE: " + currNextButton.data.getTitle() + "\n\n" + "AUTHOR: " + currNextButton.data.getAuthor() + "\n\n" + "PUBLICATION DATE: " + currNextButton.data.getPubDate() + "\n\n" + "TOTAL ARTICLE VIEWS: " + currNextButton.data.getViews());
                }
                else {
                    JOptionPane.showMessageDialog(null, "There are no more articles.",
                            "Invalid", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        previousArticleButton.addActionListener(e -> {
            //set Abstract text to previous article's abstract
            if (currNextButton.previous != null) {
                currNextButton = currNextButton.previous;
                Abstract.setText("\t" + currNextButton.data.getAbstract());
                EverythButAbs.setText("TITLE: " + currNextButton.data.getTitle() + "\n\n" + "AUTHOR: " + currNextButton.data.getAuthor() + "\n\n" + "PUBLICATION DATE: " + currNextButton.data.getPubDate() + "\n\n" + "TOTAL ARTICLE VIEWS: " + currNextButton.data.getViews());
            }
            else {
                JOptionPane.showMessageDialog(null, "There are no previous articles.",
                        "Invalid", JOptionPane.ERROR_MESSAGE);
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        SortBy.addActionListener(e -> {
            String selection = (String) SortBy.getSelectedItem();
            if (selection.equals("Number of Views")) {
                ArrayList<ResearchArticle> arrL = heapSortViews(toArrList(articleList));
                DLL artList = toLinkedList(arrL);
                articleList = artList;
                currNextButton = articleList.head;
                EverythButAbs.setText("TITLE: " + currNextButton.data.getTitle() + "\n\n" + "AUTHOR: " + currNextButton.data.getAuthor() + "\n\n" + "PUBLICATION DATE: " + currNextButton.data.getPubDate() + "\n\n" + "TOTAL ARTICLE VIEWS: " + currNextButton.data.getViews());
                Abstract.setText("\t" + currNextButton.data.getAbstract());
            }
            else if (selection.equals("Alphabetical by Title")) {
                ArrayList<ResearchArticle> arrL = heapSortAlphabetical(toArrList(articleList));
                DLL artList = toLinkedList(arrL);
                articleList = artList;
                currNextButton = articleList.head;
                EverythButAbs.setText("TITLE: " + currNextButton.data.getTitle() + "\n\n" + "AUTHOR: " + currNextButton.data.getAuthor() + "\n\n" + "PUBLICATION DATE: " + currNextButton.data.getPubDate() + "\n\n" + "TOTAL ARTICLE VIEWS: " + currNextButton.data.getViews());
                Abstract.setText("\t" + currNextButton.data.getAbstract());
            }
        });

        lightModeCheckBox.addActionListener(e -> {
            if (lightModeCheckBox.isSelected()) {
                panel1.setBackground(Color.decode("#FFFFFF"));
                OverviewLabel.setBackground(Color.decode("#FFFFFF"));
                OverviewLabel.setForeground(Color.decode("#9CC5A1"));
                AbstractLabel.setBackground(Color.decode("#FFFFFF"));
                AbstractLabel.setForeground(Color.decode("#9CC5A1"));
                EverythButAbs.setBackground(Color.decode("#FFFFFF"));
                EverythButAbs.setForeground(Color.decode("#49A078"));
                Abstract.setBackground(Color.decode("#FFFFFF"));
                Abstract.setForeground(Color.decode("#49A078"));
                DeleteInsertPanel.setBackground(Color.decode("#FFFFFF"));
                LeavePanel.setBackground(Color.decode("#FFFFFF"));
                setVisible(true);
            }
            else {
                //dark mode
                panel1.setBackground(Color.decode("#216869"));
                OverviewLabel.setBackground(Color.decode("#216869"));
                OverviewLabel.setForeground(Color.decode("#9CC5A1"));
                AbstractLabel.setBackground(Color.decode("#216869"));
                AbstractLabel.setForeground(Color.decode("#9CC5A1"));
                EverythButAbs.setBackground(Color.decode("#216869"));
                EverythButAbs.setForeground(Color.decode("#49A078"));
                Abstract.setBackground(Color.decode("#216869"));
                Abstract.setForeground(Color.decode("#49A078"));
                DeleteInsertPanel.setBackground(Color.decode("#216869"));
                LeavePanel.setBackground(Color.decode("#216869"));
                setVisible(true);
            }
        });

        deleteArticleButton.addActionListener(e -> {
            String deleteStmt = "DELETE FROM research_articles WHERE title = '" + currNextButton.data.getTitle() + "'";
            project.deleteArticle(currNextButton.data);
            JOptionPane.showMessageDialog(null, "Article deleted.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            if (currNextButton.previous == null) {
                currNextButton = currNextButton.next;
                currNextButton.previous = null;
                EverythButAbs.setText("TITLE: " + currNextButton.data.getTitle() + "\n\n" + "AUTHOR: " + currNextButton.data.getAuthor() + "\n\n" + "PUBLICATION DATE: " + currNextButton.data.getPubDate() + "\n\n" + "TOTAL ARTICLE VIEWS: " + currNextButton.data.getViews());
                Abstract.setText("\t" + currNextButton.data.getAbstract());
            }
            else if (currNextButton.next == null) {
                currNextButton = currNextButton.previous;
                currNextButton.next = null;
                EverythButAbs.setText("TITLE: " + currNextButton.data.getTitle() + "\n\n" + "AUTHOR: " + currNextButton.data.getAuthor() + "\n\n" + "PUBLICATION DATE: " + currNextButton.data.getPubDate() + "\n\n" + "TOTAL ARTICLE VIEWS: " + currNextButton.data.getViews());
                Abstract.setText("\t" + currNextButton.data.getAbstract());
            }
            else {
                currNextButton.next.previous = currNextButton.previous;
                currNextButton.previous.next = currNextButton.next;
                currNextButton = currNextButton.next;
                EverythButAbs.setText("TITLE: " + currNextButton.data.getTitle() + "\n\n" + "AUTHOR: " + currNextButton.data.getAuthor() + "\n\n" + "PUBLICATION DATE: " + currNextButton.data.getPubDate() + "\n\n" + "TOTAL ARTICLE VIEWS: " + currNextButton.data.getViews());
                Abstract.setText("\t" + currNextButton.data.getAbstract());
            }
        });

        insertArticleButton.addActionListener(e ->{
            String checkStringRegex = "[^\\p{P}|^\\d+]+";
            String title = JOptionPane.showInputDialog(null, "Enter the title of the article: ", "Insert Article", JOptionPane.QUESTION_MESSAGE);
            if (!title.matches(checkStringRegex)) {
                JOptionPane.showMessageDialog(null, "Please enter a valid title.",
                       "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String author = JOptionPane.showInputDialog(null, "Enter the author(s) of the article: ", "Insert Article", JOptionPane.QUESTION_MESSAGE);
            String pubDate = JOptionPane.showInputDialog(null, "Enter the publication date of the article (DD MMM YYYY): ", "Insert Article", JOptionPane.QUESTION_MESSAGE);
            int views = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the total number of views of the article: ", "Insert Article", JOptionPane.QUESTION_MESSAGE));
            String abstractText = JOptionPane.showInputDialog(null, "Enter the abstract of the article: ", "Insert Article", JOptionPane.QUESTION_MESSAGE);
            ResearchArticle newArticle = new ResearchArticle(title, author, pubDate, views, abstractText);
            project.insertArticle(newArticle);
            JOptionPane.showMessageDialog(null, "Article inserted.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            articleList.addNode(newArticle);
            currNextButton = articleList.tail;
            EverythButAbs.setText("TITLE: " + currNextButton.data.getTitle() + "\n\n" + "AUTHOR: " + currNextButton.data.getAuthor() + "\n\n" + "PUBLICATION DATE: " + currNextButton.data.getPubDate() + "\n\n" + "TOTAL ARTICLE VIEWS: " + currNextButton.data.getViews());
            Abstract.setText("\t" + currNextButton.data.getAbstract());
        });

        exportButton.addActionListener(e -> {
            //read all articles from database and write to a text file
            String fileName = JOptionPane.showInputDialog(null, "Enter the name of the file to export to: ", "Export", JOptionPane.QUESTION_MESSAGE);
            if (fileName == null) {
                return;
            }
            String checkStringRegex = "[^\\p{P}|^\\d+]+";
            if (!fileName.matches(checkStringRegex)) {
                JOptionPane.showMessageDialog(null, "Please enter a valid file name.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File file = new File(fileName+".txt");
            if (file.exists()) {
                JOptionPane.showMessageDialog(null, "File already exists. Try another file name.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                DLL.Node curr = articleList.head;
                while (curr != null) {
                    bw.write("\n\n");
                    bw.write("TITLE: " + curr.data.getTitle() + "\n");
                    bw.write("AUTHOR(S): " + curr.data.getAuthor() + "\n");
                    bw.write("PUBLICATION DATE: " + curr.data.getPubDate() + "\n");
                    bw.write("# OF VIEWS: " + curr.data.getViews() + "\n");
                    bw.write("ABSTRACT: " + curr.data.getAbstract() + "\n");
                    curr = curr.next;
                }
                bw.close();
                fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "File exported.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        });
    }


    /**
     * This method converts the given doubly linked list to an ArrayList for sorting purposes.
     * @param list
     * @return
     */

    public ArrayList<ResearchArticle> toArrList(DLL list) {
        ArrayList<ResearchArticle> arrList = new ArrayList<ResearchArticle>();
        DLL.Node current = list.head;
        while (current != null) {
            arrList.add(current.data);
            current = current.next;
        }
        return arrList;
    }


    /**
     * The method converts the given ArrayList to a doubly linked list for easier next and previous functionality of the article deck.
     * @param list
     * @return
     */
    public DLL toLinkedList(ArrayList<ResearchArticle> list) {
        DLL newList = new DLL();
        ListIterator<ResearchArticle> iterator = list.listIterator();
        while (iterator.hasNext()) {
            newList.addNode(iterator.next());
        }
        return newList;
    }

    /**
     * This method calls both the heapify and siftDown methods to sort the given ArrayList.
     * @param unsortedList
     * @return
     */
    public ArrayList<ResearchArticle> heapSortAlphabetical(ArrayList<ResearchArticle> unsortedList) {
        int count = unsortedList.size();
        heapifyAlphabetical(unsortedList, count);
        int end = count-1;
        while(end > 0) {
            swap(unsortedList, end, 0);
            end = end - 1;
            siftDownAlphabetical(unsortedList, 0, end);
        }
        return unsortedList;
    }

    /**
     * This method calls the siftDown method for every iteration in the ArrayList.
     * @param unsortedList
     * @param count
     */
    public void heapifyAlphabetical(ArrayList<ResearchArticle> unsortedList, int count) {
        int start = count/2 - 1;
        while(start >= 0) {
            siftDownAlphabetical(unsortedList, start, count - 1);
            start -= 1;
        }
    }

    /**
     * This method sorts sifts down or iterates down the ArrayList and calls the swap method to sort the collection.
     * @param unsortedList
     * @param start
     * @param end
     */
    public void siftDownAlphabetical(ArrayList<ResearchArticle> unsortedList, int start, int end) {
        int root = start;
        while(root*2+1 <= end) {
            int child = root*2+1;
            int swap = root;
            if(unsortedList.get(swap).getTitle().compareTo(unsortedList.get(child).getTitle()) < 0) {
                swap = child;
            }
            if(child+1 <= end && unsortedList.get(swap).getTitle().compareTo(unsortedList.get(child+1).getTitle()) < 0) {
                swap = child+1;
            }
            if(swap != root) {
                swap(unsortedList, root, swap);
                root = swap;
            }
            else {
                return;
            }
        }
    }

    /**
     * This method calls both the heapify and siftDown methods to sort the given ArrayList.
     * @param unsortedList
     * @return
     */
    public ArrayList<ResearchArticle> heapSortViews(ArrayList<ResearchArticle> unsortedList) {
        int count = unsortedList.size();
        heapifyViews(unsortedList, count);
        int end = count-1;
        while(end > 0) {
            swap(unsortedList, end, 0);
            end = end - 1;
            siftDownViews(unsortedList, 0, end);
        }
        return unsortedList;
    }

    /**
     * This method calls the siftDown method for every iteration in the ArrayList.
     * @param unsortedList
     * @param count
     */
    public void heapifyViews(ArrayList<ResearchArticle> unsortedList, int count) {
        int start = count/2 - 1;
        while(start >= 0) {
            siftDownViews(unsortedList, start, count - 1);
            start -= 1;
        }
    }

    /**
     * This method sorts sifts down or iterates down the ArrayList and calls the swap method to sort the collection.
     * @param unsortedList
     * @param start
     * @param end
     */
    public void siftDownViews(ArrayList<ResearchArticle> unsortedList, int start, int end) {
        int root = start;
        while(root*2+1 <= end) {
            int child = root*2+1;
            int swap = root;
            if(unsortedList.get(swap).getViews() < unsortedList.get(child).getViews()) {
                swap = child;
            }
            if(child+1 <= end && unsortedList.get(swap).getViews() < unsortedList.get(child+1).getViews()) {
                swap = child+1;
            }
            if(swap != root) {
                swap(unsortedList, root, swap);
                root = swap;
            }
            else {
                return;
            }
        }
    }

    /**
     * This method swaps the two elements in the given ArrayList.
     * @param unsortedList
     * @param swapOne
     * @param swapTwo
     */
    public void swap(ArrayList<ResearchArticle> unsortedList, int swapOne, int swapTwo) {
        ResearchArticle holder = unsortedList.get(swapOne);
        unsortedList.set(swapOne, unsortedList.get(swapTwo));
        unsortedList.set(swapTwo, holder);
    }


    public static void main(String args[]) throws IOException {
        new ArticleCard();
    }
}