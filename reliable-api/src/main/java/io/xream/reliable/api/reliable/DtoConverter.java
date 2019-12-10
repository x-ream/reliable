package io.xream.reliable.api.reliable;

import io.xream.reliable.bean.dto.ReliableDto;

public interface DtoConverter {

    ReliableDto convertOnConsumed(Object message);
}
