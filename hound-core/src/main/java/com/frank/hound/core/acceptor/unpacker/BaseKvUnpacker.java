package com.frank.hound.core.acceptor.unpacker;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.frank.hound.core.context.TraceContext;
import com.frank.hound.core.event.SortEvent;
import com.frank.hound.core.event.UnpackedEvent;
import com.frank.hound.core.support.Hound;
import com.frank.hound.core.support.Sheepehound;
import com.frank.hound.core.support.TraceContextAssistant;
import com.frank.hound.core.support.TraceContextThreadLocalKeeper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author frank
 */
@Slf4j
public abstract class BaseKvUnpacker implements Unpacker
{
    private Hound hound = Sheepehound.getHound();

    @Override
    public void unpack(@NonNull Map<String, Object> unpackKvMapper)
    {
        try
        {
            TransmittableThreadLocal<TraceContext> traceContextThreadLocal = TraceContextThreadLocalKeeper.TRACE_LOCAL_CONTEXT;

            for (Map.Entry<String, Object> unpackMaterial : unpackKvMapper
                    .entrySet())
            {
                final String k = unpackMaterial.getKey();
                final String v = unpackMaterial.getValue();
                if (TraceContextAssistant.isTraceKeyContain(k))
                {
                    //放入TraceContext
                    TraceContext traceContext = new TraceContext();
                    traceContext.addContext(k, v);
                    traceContextThreadLocal.set(traceContext);
                }
            }

            hound.publishEvent(new UnpackedEvent(hound));

            hound.publishEvent(new SortEvent(hound,hound::sort));
        }
        catch (Exception e)
        {
            log.error("Hound BaseKvUnpacker error:",e);
        }
    }
}
