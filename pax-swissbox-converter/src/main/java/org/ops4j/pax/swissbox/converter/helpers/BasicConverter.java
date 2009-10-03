/*
 * Copyright 2009 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.swissbox.converter.helpers;

import org.osgi.service.blueprint.container.Converter;
import org.ops4j.pax.swissbox.converter.GenericType;
import static org.ops4j.pax.swissbox.converter.basic.AssignableConverter.*;
import static org.ops4j.pax.swissbox.converter.basic.FromNullConverter.*;
import org.ops4j.pax.swissbox.converter.basic.FromStringConverter;
import static org.ops4j.pax.swissbox.converter.basic.FromStringConverter.*;
import org.ops4j.pax.swissbox.converter.basic.FromStringToClassConverter;
import static org.ops4j.pax.swissbox.converter.basic.FromStringToClassConverter.*;
import static org.ops4j.pax.swissbox.converter.basic.ToArrayConverter.*;
import org.ops4j.pax.swissbox.converter.basic.ToCollectionConverter;
import static org.ops4j.pax.swissbox.converter.basic.ToCollectionConverter.*;
import org.ops4j.pax.swissbox.converter.basic.ToDictionaryConverter;
import static org.ops4j.pax.swissbox.converter.basic.ToDictionaryConverter.*;
import org.ops4j.pax.swissbox.converter.basic.ToMapConverter;
import static org.ops4j.pax.swissbox.converter.basic.ToMapConverter.*;
import static org.ops4j.pax.swissbox.converter.basic.ToNumberConverter.*;
import static org.ops4j.pax.swissbox.converter.helpers.ImmutableCompositeConverter.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class BasicConverter
    extends WrapperConverter
    implements Converter
{

    public BasicConverter()
    {
        delegate(
            immutableCompositeConverter(
                fromNullConverter(),
                assignableConverter(),
                toNumberConverter(),
                toArrayConverter( this ),
                toCollectionConverter( this ),
                toDictionaryConverter( this ),
                toMapConverter( this ),
                fromStringConverter()
            )
        );
    }

    public BasicConverter( final Converter escape )
    {
        delegate(
            immutableCompositeConverter(
                fromNullConverter(),
                assignableConverter(),
                toNumberConverter(),
                toArrayConverter( escape ),
                toCollectionConverter( escape ),
                toDictionaryConverter( escape ),
                toMapConverter( escape ),
                fromStringConverter()
            )
        );
    }

    public BasicConverter( final GenericType.Loader loader )
    {
        delegate(
            immutableCompositeConverter(
                fromNullConverter(),
                assignableConverter(),
                toNumberConverter(),
                toArrayConverter( this ),
                toCollectionConverter( this ),
                toDictionaryConverter( this ),
                toMapConverter( this ),
                fromStringConverter(),
                fromStringToClassConverter( loader )
            )
        );
    }

    public BasicConverter( final Converter escape,
                           final GenericType.Loader loader )
    {
        delegate(
            immutableCompositeConverter(
                fromNullConverter(),
                assignableConverter(),
                toNumberConverter(),
                toArrayConverter( escape ),
                toCollectionConverter( escape ),
                toDictionaryConverter( escape ),
                toMapConverter( escape ),
                fromStringConverter(),
                fromStringToClassConverter( loader )
            )
        );
    }

}