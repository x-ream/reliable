package io.xream.reliable.remote.reliable;

import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.x7.reyc.ReyClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@ReyClient("http://${reliable.app}/failed" )
public interface FailedServiceRemote {


    @RequestMapping(value = "/find", method = RequestMethod.GET)
    List<ReliableMessage> findFailed();

    @RequestMapping(value = "/find/{topic}", method = RequestMethod.GET)
    List<ReliableMessage> findFailedByTopic();

    @RequestMapping(value = "/retryAll", method = RequestMethod.GET)
    boolean retryAll();

    @RequestMapping(value = "/retry/{messageId}", method = RequestMethod.GET)
    boolean retry(String messageId);

}
