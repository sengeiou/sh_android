package com.shootr.android.domain.asserts;

import com.shootr.android.domain.User;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.util.Objects;

/**
 * {@link User} specific assertions - Generated by CustomAssertionGenerator.
 */
public class UserAssert extends AbstractAssert<UserAssert, User> {

  /**
   * Creates a new <code>{@link UserAssert}</code> to make assertions on actual User.
   * @param actual the User we want to make assertions on.
   */
  public UserAssert(User actual) {
    super(actual, UserAssert.class);
  }

  /**
   * An entry point for UserAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one can write directly: <code>assertThat(myUser)</code> and get specific assertion with code completion.
   * @param actual the User we want to make assertions on.
   * @return a new <code>{@link UserAssert}</code>
   */
  public static UserAssert assertThat(User actual) {
    return new UserAssert(actual);
  }

  /**
   * Verifies that the actual User's bio is equal to the given one.
   * @param bio the given bio to compare the actual User's bio to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's bio is not equal to the given one.
   */
  public UserAssert hasBio(String bio) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected bio of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualBio = actual.getBio();
    if (!Objects.areEqual(actualBio, bio)) {
      failWithMessage(assertjErrorMessage, actual, bio, actualBio);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's email is equal to the given one.
   * @param email the given email to compare the actual User's email to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's email is not equal to the given one.
   */
  public UserAssert hasEmail(String email) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected email of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualEmail = actual.getEmail();
    if (!Objects.areEqual(actualEmail, email)) {
      failWithMessage(assertjErrorMessage, actual, email, actualEmail);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User is follower.
   * @return this assertion object.
   * @throws AssertionError - if the actual User is not follower.
   */
  public UserAssert isFollower() {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // check
    if (!actual.isFollower()) {
      failWithMessage("\nExpected actual User to be follower but was not.");
    }
    
    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User is not follower.
   * @return this assertion object.
   * @throws AssertionError - if the actual User is follower.
   */
  public UserAssert isNotFollower() {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // check
    if (actual.isFollower()) {
      failWithMessage("\nExpected actual User not to be follower but was.");
    }
    
    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User is following.
   * @return this assertion object.
   * @throws AssertionError - if the actual User is not following.
   */
  public UserAssert isFollowing() {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // check
    if (!actual.isFollowing()) {
      failWithMessage("\nExpected actual User to be following but was not.");
    }
    
    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User is not following.
   * @return this assertion object.
   * @throws AssertionError - if the actual User is following.
   */
  public UserAssert isNotFollowing() {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // check
    if (actual.isFollowing()) {
      failWithMessage("\nExpected actual User not to be following but was.");
    }
    
    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's idUser is equal to the given one.
   * @param idUser the given idUser to compare the actual User's idUser to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's idUser is not equal to the given one.
   */
  public UserAssert hasIdUser(Long idUser) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected idUser of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    Long actualIdUser = Long.valueOf(actual.getIdUser());
    if (!Objects.areEqual(actualIdUser, idUser)) {
      failWithMessage(assertjErrorMessage, actual, idUser, actualIdUser);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User is me.
   * @return this assertion object.
   * @throws AssertionError - if the actual User is not me.
   */
  public UserAssert isMe() {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // check
    if (!actual.isMe()) {
      failWithMessage("\nExpected actual User to be me but was not.");
    }
    
    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User is not me.
   * @return this assertion object.
   * @throws AssertionError - if the actual User is me.
   */
  public UserAssert isNotMe() {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // check
    if (actual.isMe()) {
      failWithMessage("\nExpected actual User not to be me but was.");
    }
    
    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's name is equal to the given one.
   * @param name the given name to compare the actual User's name to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's name is not equal to the given one.
   */
  public UserAssert hasName(String name) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected name of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualName = actual.getName();
    if (!Objects.areEqual(actualName, name)) {
      failWithMessage(assertjErrorMessage, actual, name, actualName);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's numFollowers is equal to the given one.
   * @param numFollowers the given numFollowers to compare the actual User's numFollowers to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's numFollowers is not equal to the given one.
   */
  public UserAssert hasNumFollowers(Long numFollowers) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected numFollowers of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    Long actualNumFollowers = actual.getNumFollowers();
    if (!Objects.areEqual(actualNumFollowers, numFollowers)) {
      failWithMessage(assertjErrorMessage, actual, numFollowers, actualNumFollowers);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's numFollowings is equal to the given one.
   * @param numFollowings the given numFollowings to compare the actual User's numFollowings to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's numFollowings is not equal to the given one.
   */
  public UserAssert hasNumFollowings(Long numFollowings) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected numFollowings of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    Long actualNumFollowings = actual.getNumFollowings();
    if (!Objects.areEqual(actualNumFollowings, numFollowings)) {
      failWithMessage(assertjErrorMessage, actual, numFollowings, actualNumFollowings);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's photo is equal to the given one.
   * @param photo the given photo to compare the actual User's photo to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's photo is not equal to the given one.
   */
  public UserAssert hasPhoto(String photo) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected photo of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualPhoto = actual.getPhoto();
    if (!Objects.areEqual(actualPhoto, photo)) {
      failWithMessage(assertjErrorMessage, actual, photo, actualPhoto);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's points is equal to the given one.
   * @param points the given points to compare the actual User's points to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's points is not equal to the given one.
   */
  public UserAssert hasPoints(Long points) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected points of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    Long actualPoints = actual.getPoints();
    if (!Objects.areEqual(actualPoints, points)) {
      failWithMessage(assertjErrorMessage, actual, points, actualPoints);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's username is equal to the given one.
   * @param username the given username to compare the actual User's username to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's username is not equal to the given one.
   */
  public UserAssert hasUsername(String username) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected username of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualUsername = actual.getUsername();
    if (!Objects.areEqual(actualUsername, username)) {
      failWithMessage(assertjErrorMessage, actual, username, actualUsername);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's visibleEventId is equal to the given one.
   * @param visibleEventId the given visibleEventId to compare the actual User's visibleEventId to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's visibleEventId is not equal to the given one.
   */
  public UserAssert hasVisibleEventId(String visibleEventId) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected visibleEventId of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualVisibleEventId = actual.getIdWatchingEvent();
    if (!Objects.areEqual(actualVisibleEventId, visibleEventId)) {
      failWithMessage(assertjErrorMessage, actual, visibleEventId, actualVisibleEventId);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's visibleEventTitle is equal to the given one.
   * @param visibleEventTitle the given visibleEventTitle to compare the actual User's visibleEventTitle to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's visibleEventTitle is not equal to the given one.
   */
  public UserAssert hasVisibleEventTitle(String visibleEventTitle) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected visibleEventTitle of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualVisibleEventTitle = actual.getWatchingEventTitle();
    if (!Objects.areEqual(actualVisibleEventTitle, visibleEventTitle)) {
      failWithMessage(assertjErrorMessage, actual, visibleEventTitle, actualVisibleEventTitle);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's website is equal to the given one.
   * @param website the given website to compare the actual User's website to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's website is not equal to the given one.
   */
  public UserAssert hasWebsite(String website) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected website of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualWebsite = actual.getWebsite();
    if (!Objects.areEqual(actualWebsite, website)) {
      failWithMessage(assertjErrorMessage, actual, website, actualWebsite);
    }

    // return the current assertion for method chaining
    return this;
  }

}
