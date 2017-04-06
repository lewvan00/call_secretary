/*
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package amazonaws.services.lexrts.model.transform;

import com.amazonaws.transform.SimpleTypeJsonUnmarshallers.*;
import com.amazonaws.transform.*;
import com.amazonaws.util.json.AwsJsonReader;

/**
 * JSON unmarshaller for POJO Button
 */
class ButtonJsonUnmarshaller implements Unmarshaller<amazonaws.services.lexrts.model.Button, JsonUnmarshallerContext> {

    public amazonaws.services.lexrts.model.Button unmarshall(JsonUnmarshallerContext context) throws Exception {
        AwsJsonReader reader = context.getReader();
        if (!reader.isContainer()) {
            reader.skipValue();
            return null;
        }
        amazonaws.services.lexrts.model.Button button = new amazonaws.services.lexrts.model.Button();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("text")) {
                button.setText(StringJsonUnmarshaller.getInstance()
                        .unmarshall(context));
            } else if (name.equals("value")) {
                button.setValue(StringJsonUnmarshaller.getInstance()
                        .unmarshall(context));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return button;
    }

    private static ButtonJsonUnmarshaller instance;

    public static ButtonJsonUnmarshaller getInstance() {
        if (instance == null)
            instance = new ButtonJsonUnmarshaller();
        return instance;
    }
}
