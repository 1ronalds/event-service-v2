package eventservice.eventservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Model of the event type data")
public class EventTypeDto {

    @ApiModelProperty(value = "Unique id of the event type")
    private Long id;

    @ApiModelProperty(value = "Event type")
    private String type;
}
