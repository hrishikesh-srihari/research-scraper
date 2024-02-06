import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ATDSProject {

    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE



    static List<WebElement> results = new ArrayList<WebElement>();
    static ArrayList<String> titles = new ArrayList<String>();
    static ArrayList<String> authors = new ArrayList<String>();
    static ArrayList<String> pubDates = new ArrayList<String>();
    static ArrayList<String> abstracts = new ArrayList<String>();
    static ArrayList<Integer> views = new ArrayList<Integer>();
    static ArrayList<ResearchArticle> articles = new ArrayList<ResearchArticle>();
    static WebDriver driver = new FirefoxDriver();


    /**
     * This method is used to read in the search terms/keywords from the given text file and
     * returns a formatted string that can be used to search on the Taylor and Francis site.
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public static String getKeywords(String fileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileName));
        String kws = "";
        while (sc.hasNext()) {
            kws += sc.nextLine() + " AND ";
        }
        kws = kws.substring(0, kws.length() - 5);
        return kws;
    }

    /**
     * This method will set up the Selenium WebDriver tool for web scraping the data from the Taylor and Francis site
     * @throws FileNotFoundException
     */

    public static void setupScrape() throws FileNotFoundException {
        System.setProperty("webdriver.gecko.driver", "/Users/rishisrihari/Documents/SeleniumProject/geckodriver");

        //implicit wait
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        //URL launch
        driver.get("https://www.tandfonline.com/");

        //identify element
        WebElement d = driver.findElement(By.xpath("//*[@id=\"searchText-1d85a42e-ad57-4c0d-a477-c847718bcb5d\"]"));

        driver.findElement(By.xpath("//*[@id=\"cea739ac-da2c-4d77-9cf1-cb3e0da7e31e\"]/div/div/div/a")).click();
        d.sendKeys(getKeywords("NormalKeywords.txt"));
        d.sendKeys(Keys.ENTER);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"frmSearchResults\"]/ol")));
        String currUrl = driver.getCurrentUrl();
        driver.navigate().to(currUrl + "&startPage=&pageSize=50");
        //get all article in results and store in a list
        results = driver.findElements(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li/article"));
    }

    /**
     * This method will scrape the Taylor and Francis site based on the given keywords and will store the results in the database and will return the of articles found in
     * the form of a doubly linked list (for the purposes of the article viewer GUI)
     * @return DLL of ResearchArticles
     */
    public static DLL getDataAndStoreInDB() {
       DLL articlesFromDB = null;
        try {
            //connect to sqlite database
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:articles.db");
            Statement stmt = conn.createStatement();
            System.out.println(GREEN_BOLD + "Opened database successfully" + RESET);

            //create table if not exists

            String createTable = "CREATE TABLE IF NOT EXISTS research_articles (title TEXT UNIQUE, author TEXT, pub_date TEXT, views INTEGER, abstract TEXT)";
            stmt.executeUpdate(createTable);
            System.out.println(GREEN_BOLD + "Table created successfully" + RESET);


            for (int i = 0; i < results.size(); i++) {
                WebElement we = results.get(i);
                int x = i + 1;
                if (we.findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x + "]/article/div[5]")).getText().contains("Abstract")) {
                    titles.add(we.getAttribute("data-title"));
                    String fullPubDate = we.findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x + "]" +
                                    "/article/div[3]/div[3]/span[@class='publication-year']")).getText();
                    pubDates.add(fullPubDate.substring(18));
                    authors.add(we.findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x + "]/article/div[3]/div[1]")).getText());

                    String tmpViews = "";
                    if (we.findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x +"]/article/div[3]/div[3]/span[2]")).getText()
                            .contains("Views")){
                        tmpViews = we.findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x + "]/article/div[3]/div[3]/span[2]")).getText();
                    }
                    else if (we.findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x +"]/article/div[3]/div[3]/span[3]")).getText()
                            .contains("Views")) {
                        tmpViews = we.findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x + "]/article/div[3]/div[3]/span[3]")).getText();
                    }


                    String numberOnly= tmpViews.replaceAll("[^0-9]", "");
                    views.add(Integer.parseInt(numberOnly));

                    we.findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x + "]/article/div[5]/button")).click();
                }
                System.out.println("\n");
            }

            for (int i = 0; i < results.size(); i++) {
                int x = i + 1;
                if (results.get(i).findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[2]/article/div[5]")).getText().contains("Abstract") &&
                        results.get(i).findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x + "]/article/div[5]")).getText()
                                .contains("Abstract")) {
                    abstracts.add(results.get(i).findElement(By.xpath("//*[@id=\"frmSearchResults\"]/ol/li[" + x + "]" +
                            "/article/div[5]/div/div[@class='abstractSection']")).getText() + "\n");
                }
            }

            System.out.println("title size:" + titles.size());
            System.out.println("author size:" + authors.size());
            System.out.println("pubDate size:" + pubDates.size());
            System.out.println("views size:" + views.size());
            System.out.println("abstract size:" + abstracts.size());

            for (int i = 0; i < titles.size(); i++) {
                articles.add(new ResearchArticle(titles.get(i), authors.get(i), pubDates.get(i), views.get(i), abstracts.get(i)));
            }

            //clear the table
            String clearTable = "DELETE FROM research_articles WHERE title IS NOT NULL";
            stmt.executeUpdate(clearTable);
            System.out.println(GREEN_BOLD + "Table cleared successfully" + RESET);



            //insert articles into the database
            try {
                System.out.println("Articles size: " + articles.size());
                for (int i = 0; i < articles.size(); i++) {
                    String title = articles.get(i).getTitle();
                    String author = articles.get(i).getAuthor();
                    String pubDate = articles.get(i).getPubDate();
                    int views = articles.get(i).getViews();
                    String abs = articles.get(i).getAbstract();
                    //insert values into the database
                    String insert = "INSERT OR REPLACE INTO research_articles (title, author, pub_date, views, abstract) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insert);
                    pstmt.setString(1, title);
                    pstmt.setString(2, author);
                    pstmt.setString(3, pubDate);
                    pstmt.setInt(4, views);
                    pstmt.setString(5, abs);
                    pstmt.executeUpdate();
                }
                System.out.println(GREEN_BOLD + "Inserted successfully" + RESET);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            driver.close();


            articlesFromDB = new DLL();
            try {
                String selectStmt = "SELECT * FROM research_articles WHERE title IS NOT NULL";
                ResultSet rs = stmt.executeQuery(selectStmt);
                while (rs.next()) {
                    ResearchArticle article = new ResearchArticle(rs.getString("title"), rs.getString("author"), rs.getString("pub_date"), rs.getInt("views"), rs.getString("abstract"));
                    articlesFromDB.addNode(article);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            return articlesFromDB;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return articlesFromDB;
    }

    /**
     * This method will delete the given ResearchArticle from the SQLite database
     * @param article
     */
    public static void deleteArticle(ResearchArticle article) {
        try {
            String deleteStmt = "DELETE FROM research_articles WHERE title = ?";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:articles.db");
            PreparedStatement pstmt = conn.prepareStatement(deleteStmt);
            pstmt.setString(1, article.getTitle());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * This method will add/update the given ResearchArticle in the SQLite database
     * @param article
     */
    public static void insertArticle(ResearchArticle article) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:articles.db");
            String title = article.getTitle();
            String author = article.getAuthor();
            String pubDate = article.getPubDate();
            int views = article.getViews();
            String abs = article.getAbstract();
            //insert values into the database
            String insert = "INSERT OR REPLACE INTO research_articles (title, author, pub_date, views, abstract) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insert);
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, pubDate);
            pstmt.setInt(4, views);
            pstmt.setString(5, abs);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}