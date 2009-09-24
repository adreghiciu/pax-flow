package org.ops4j.pax.flow.runtime.internal;

import java.util.HashSet;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionProperty;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.helpers.DefaultExecutionContext;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
class PersistentExecutionContext
    extends DefaultExecutionContext
    implements ExecutionContext
{

    private ExecutionContext m_transientContext;

    protected PersistentExecutionContext( final ExecutionProperty<?>... properties )
    {
        super( properties );
    }

    public PersistentExecutionContext useTransientContext( final ExecutionContext context )
    {
        m_transientContext = context;

        return this;
    }

    @Override
    public <T> T get( final PropertyName name )
    {
        T value = super.<T>get( name );
        if( value == null )
        {
            value = m_transientContext.<T>get( name );
        }
        return value;
    }

    @Override
    public <T> T get( final PropertyName name, final T defaultValue )
    {
        T value = super.<T>get( name );
        if( value == null )
        {
            value = m_transientContext.<T>get( name );
        }
        if( value == null )
        {
            value = super.<T>get( name, defaultValue );
        }
        return value;
    }

    @Override
    public Iterable<PropertyName> names()
    {
        final HashSet<PropertyName> names = new HashSet<PropertyName>();

        final Iterable<PropertyName> persistenNames = super.names();
        for( PropertyName name : persistenNames )
        {
            names.add( name );
        }

        final Iterable<PropertyName> transientNames = super.names();
        for( PropertyName name : transientNames )
        {
            names.add( name );
        }

        return names;
    }

    @Override
    public ExecutionContext add( final ExecutionProperty property )
    {
        if( property.isPersistent() )
        {
            super.add( property );
        }
        else
        {
            m_transientContext.add( property );
        }

        return this;
    }

}
