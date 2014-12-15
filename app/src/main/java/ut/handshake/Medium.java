package ut.handshake;

public class Medium {

    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    String content = null;
    Boolean isEmail = false;
    Boolean selected = false;

    public Medium(String content, boolean isEmail) {
        this.content = content;
        this.isEmail = isEmail;
    }

    public String getType() {
        if(isEmail){
            return PHONE;
        } else {
            return EMAIL;
        }
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsEmail() {
        return isEmail;
    }

    public void setIsEmail(Boolean isEmail) {
        this.isEmail = isEmail;
    }
}
