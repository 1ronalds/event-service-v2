package eventservice.eventservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Model of the event data that contains only id and event title")
public class EventMinimalDto {

    @ApiModelProperty(value = "Unique id of the event")
    private Long id;

    @ApiModelProperty(value = "Title of the event")
    private String title;
}
