package dev.ikm.tinkar.composer.test;

/*
 * Copyright © 2024 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dev.ikm.tinkar.common.id.PublicIds;
import dev.ikm.tinkar.common.service.CachingService;
import dev.ikm.tinkar.common.service.PrimitiveData;
import dev.ikm.tinkar.common.service.ServiceKeys;
import dev.ikm.tinkar.common.service.ServiceProperties;
import dev.ikm.tinkar.composer.Composer;
import dev.ikm.tinkar.composer.Session;
import dev.ikm.tinkar.composer.constituent.Comment;
import dev.ikm.tinkar.composer.constituent.FullyQualifiedName;
import dev.ikm.tinkar.composer.constituent.Synonym;
import dev.ikm.tinkar.composer.constituent.USEnglishDialect;
import dev.ikm.tinkar.terms.EntityProxy;
import dev.ikm.tinkar.terms.EntityProxy.Concept;
import dev.ikm.tinkar.terms.State;
import dev.ikm.tinkar.terms.TinkarTerm;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ComposerCreateIT {

    private final Path datastore = Path.of(System.getProperty("user.dir"))
            .resolve("target")
            .resolve(ComposerCreateIT.class.getSimpleName())
            .resolve("datastore");

    @BeforeAll
    public void beforeAll() throws IOException {
//        Files.createDirectories(datastore);
        CachingService.clearAll();
//        ServiceProperties.set(ServiceKeys.DATA_STORE_ROOT, datastore.toFile());
        PrimitiveData.selectControllerByName("Load Ephemeral Store");
        PrimitiveData.start();
    }

    @AfterAll
    public void afterAll() {
        PrimitiveData.stop();
    }

    @Test
    public void createSessionTest() {
        State status = State.ACTIVE;
        long time = System.currentTimeMillis();
        Concept author = Concept.make(PublicIds.newRandom());
        Concept module = Concept.make(PublicIds.newRandom());
        Concept path = Concept.make(PublicIds.newRandom());

        try(Session session = new Session(status, time, author, module, path)){
            Composer composer = session.makeCompose();

            composer.concept(Concept.make("Concept", PublicIds.newRandom()))
                    .with(new FullyQualifiedName(EntityProxy.Semantic.make("F1", PublicIds.newRandom()), TinkarTerm.ENGLISH_LANGUAGE, "FQN1", TinkarTerm.DESCRIPTION_NOT_CASE_SENSITIVE)
                            .with(new USEnglishDialect(EntityProxy.Semantic.make("D1F1", PublicIds.newRandom()), TinkarTerm.PREFERRED)
                                    .with(new Comment(EntityProxy.Semantic.make("C1D1", PublicIds.newRandom()), "Comment on USEnglishDialect")))
                            .with(new Comment(EntityProxy.Semantic.make("C1F1", PublicIds.newRandom()), "Comment on FQN1")))
                    .with(new Comment(EntityProxy.Semantic.make("C1", PublicIds.newRandom()), "Comment1 on Concept"))
                    .with(
                            new FullyQualifiedName(EntityProxy.Semantic.make("F2", PublicIds.newRandom()), TinkarTerm.ENGLISH_LANGUAGE, "FQN2", TinkarTerm.DESCRIPTION_NOT_CASE_SENSITIVE),
                            new Comment(EntityProxy.Semantic.make("C2", PublicIds.newRandom()), "Comment2 on Concept"))
                    .with(new Synonym(EntityProxy.Semantic.make("S1", PublicIds.newRandom()), TinkarTerm.ENGLISH_LANGUAGE, "Synonym", TinkarTerm.DESCRIPTION_NOT_CASE_SENSITIVE)
                            .with(
                                    new Comment(EntityProxy.Semantic.make("C1S1", PublicIds.newRandom()), "Comment1 on Synonym"),
                                    new Comment(EntityProxy.Semantic.make("C2S1", PublicIds.newRandom()), "Comment2 on Synonym")));
        }
    }
}
