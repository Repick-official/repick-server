package repick.repickserver.infra.slack.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repick.repickserver.infra.slack.application.SlackApiService;
import repick.repickserver.infra.slack.dto.SlackApiDto;

@RestController
@RequestMapping("/slack")
@RequiredArgsConstructor
public class SlackApiController {

    private final SlackApiService slackApiService;

    @PostMapping(value = "/subscribe/add")
    public void addSubscribeRequest(@RequestBody SlackApiDto request) {
        slackApiService.addSubscribeRequest(request);
    }

    @PostMapping(value = "/subscribe/deny")
    public void denySubscribeRequest(@RequestBody SlackApiDto request) {
        slackApiService.denySubscribeRequest(request);
    }

}
