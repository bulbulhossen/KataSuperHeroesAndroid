/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

    private static final int N_SUPER_HEROES = 5;

    @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
  }

  @Test
  public void doesNotShowEmptyCaseIfThereAreSuperHeroes() throws Exception {
    givenThereAreSomeSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())));
  }

    @Test
    public void showsExactlyOneSuperHeroIfThereIsOneSuperHero() throws Exception {
        givenThereIsOneSuperHero();

        startActivity();

        RecyclerViewItemsCountMatcher matcher = new RecyclerViewItemsCountMatcher(1);
        onView(withId(R.id.recycler_view)).check(matches(matcher));
    }

    // Test con 10 super heroes, y que la info que se muestra es correcta

    private void givenThereIsOneSuperHero() {
        givenThereAreSomeSuperHeroes(1, false);
    }

    private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

    private List<SuperHero> givenThereAreSomeSuperHeroes() {
        return givenThereAreSomeSuperHeroes(N_SUPER_HEROES);
    }

    private List<SuperHero> givenThereAreSomeSuperHeroes(int numberOfSuperHeroes) {
        return givenThereAreSomeSuperHeroes(N_SUPER_HEROES, false);
    }

    private List<SuperHero> givenThereAreSomeSuperHeroes(int numberOfSuperHeroes, boolean avengers) {
        List<SuperHero> superHeroes = new LinkedList<>();
        for (int i = 0; i < numberOfSuperHeroes; i++) {
            String superHeroName = "SuperHero - " + i;
            String superHeroPhoto = "https://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg";
            String superHeroDescription = "Description Super Hero - " + i;
            SuperHero superHero =
                    new SuperHero(superHeroName, superHeroPhoto, avengers, superHeroDescription);
            superHeroes.add(superHero);
            when(repository.getByName(superHeroName)).thenReturn(superHero);
        }
        when(repository.getAll()).thenReturn(superHeroes);
        return superHeroes;
    }


  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}