/**
 * Copyright 2018 bejson.com
 */
package edgar.wk.face.dto;
import java.util.List;

/**
 * Auto-generated: 2018-07-09 19:23:14
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class FaceDto {

    private String image_id;
    private String request_id;
    private int time_used;
    private List<Hand> hands;
    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }
    public String getImage_id() {
        return image_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }
    public String getRequest_id() {
        return request_id;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }
    public int getTime_used() {
        return time_used;
    }

    public void setHands(List<Hand> hands) {
        this.hands = hands;
    }
    public List<Hand> getHands() {
        return hands;
    }

}