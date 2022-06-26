package order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Orders {

    private String[] ingredients;
    private String _id;
    private String owner;
    private String status;
    private String name;
    private String createdAt;
    private String updatedAt;
    private Integer number;
}
