package com.hbfintech.hound.core.support;

import com.hbfintech.hound.core.acceptor.sorter.Sorter;
import com.hbfintech.hound.core.acceptor.sorter.SorterInitializer;
import lombok.NonNull;

/**
 * 初始化并持有容器等重要实例的上下文，作用类似于ApplicationContext
 * @author frank
 */
public class HoundComponentContext implements HoundContext
{
    private static HoundContext context = new HoundComponentContext();

    private HoundComponentRegistry componentContainer;

    private Sorter firstSorter;

    private HoundComponentContext()
    {
        componentContainer = new HoundComponentRegistry();
        SorterInitializer sorterLoader= new SorterInitializer();
        firstSorter = sorterLoader.getFirstSorter();
    }

    @Override
    public <T> T getComponent(@NonNull String componentName,@NonNull Class<T> componentClazz)
    {
        HoundComponentRegistry.HoundComponentGroup<T> basicContainer = componentContainer.getComponentsGroup(componentClazz);
        if(basicContainer!=null)
        {
            return basicContainer.get(componentName);
        }
        return null;
    }

    public static HoundContext getContext()
    {
        return context;
    }

    @Override
    public void sort()
    {
        //TODO： 待优化，准备使用单线程事件驱动模型，正在斟酌如何实现
        firstSorter.sort();
    }
}
