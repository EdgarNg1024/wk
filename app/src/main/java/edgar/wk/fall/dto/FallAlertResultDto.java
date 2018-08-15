/**
  * Copyright 2018 bejson.com 
  */
package edgar.wk.fall.dto;
import java.util.List;

/**
 * Auto-generated: 2018-08-15 21:56:55
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class FallAlertResultDto {

    private int time_used;
    private String image_id;
    private List<Skeleton> skeletons;
    private String request_id;
    public void setTime_used(int time_used) {
         this.time_used = time_used;
     }
     public int getTime_used() {
         return time_used;
     }

    public void setImage_id(String image_id) {
         this.image_id = image_id;
     }
     public String getImage_id() {
         return image_id;
     }

    public void setSkeletons(List<Skeleton> skeletons) {
         this.skeletons = skeletons;
     }
     public List<Skeleton> getSkeletons() {
         return skeletons;
     }

    public void setRequest_id(String request_id) {
         this.request_id = request_id;
     }
     public String getRequest_id() {
         return request_id;
     }

}