/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.rocketmq.spring.boot.config;

import java.util.Map;

/**
 * Strategy for providing the topic-to-selector-expression subscription map used
 * by the push consumer when it subscribes to topics.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public interface SubscriptionProvider {

	/**
	 * @return a map of topic to selector expression (tag or SQL92) describing the
	 *         subscriptions to register
	 */
	Map<String /* topic */, String /* selectorExpress */> subscription();

}
