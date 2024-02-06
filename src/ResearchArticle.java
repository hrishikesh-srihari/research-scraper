public class ResearchArticle {
    private String title;
    private String author;
    private String publicationDate;
    private int views;
    private String abstractText;

    public ResearchArticle(String title, String author, String publicationDate, int views, String abstractText) {
        this.title = title;
        this.author = author;
        this.views = views;
        this.publicationDate = publicationDate;
        this.abstractText = abstractText;
    }

    public static void main(String args[]) {
    }

    /**
     * This method returns the number of views for the article.
     * @return views
     */
    public int getViews() {
        return views;
    }

    /**
     * This method returns the title of the article.
     * @return title
     */

    public String getTitle() {
        return title;
    }

    /**
     * This method returns the author of the article.
     * @return author
     */

    public String getAuthor() {
        return author;
    }

    /**
     * This method returns the publication date of the article.
     * @return publicationDate
     */

    public String getPubDate() {
        return publicationDate;
    }

    /**
     * This method returns the abstract text of the article.
     * @return abstractText
     */

    public String getAbstract() {
        return abstractText;
    }

    /**
     * This method returns the ResearchArticle in String form.
     * @return ResearchArticle
     */
    //implement toString method
    public String toString() {
        return "Title: " + title + "\n" + "Author: " + author + "\n" + "Publication Date: " + publicationDate + "\n" + "Views: " + views + "\n" + "Abstract: " + abstractText + "\n\n";
    }

}
