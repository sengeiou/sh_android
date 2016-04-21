package com.shootr.mobile.data.api.exception;

public enum ErrorInfo {

    InvalidSocialTokenException(1001, "invalid social token", 401),
    InvalidTokenException(1002, "invalid token", 401),
    PasswordExpiredException(1003, "Password Expired", 401),
    UnauthorizedRequestException(1004, "Unauthorized", 401),
    AccountStatusException(1005, "Invalid account status", 401),
    AuthenticationCredentialsNotFoundException(1006, "Authentication credentials not found", 401),
    AuthenticationServiceException(1007, "Authentication service error", 401),
    BadCredentialsException(1008, "Invalid credentials", 401),
    ExpiredAuthorizationException(1009, "Expired Authorization", 401),
    InsufficientAuthenticationException(1010, "Insufficient authentication", 401),
    InternalAuthenticationServiceException(1011, "Internal authentication service error", 401),
    NonceExpiredException(1012, "Nonce expired", 401),
    PreAuthenticatedCredentialsNotFoundException(1013, "PreAuthenticated credentials not found", 401),
    ProviderNotFoundException(1014, "Provider not found", 401),
    RememberMeAuthenticationException(1015, "Remember me authentication error", 401),
    SessionAuthenticationException(1016, "Session authentication error", 401),
    UsernameNotFoundException(1017, "Username not found", 401),
    RevokedAuthorizationException(1018, "Revoked authorization", 401),
    RejectedAuthorizationException(1019, "Rejected authorization", 401),

    InvalidRequestException(2001, "invalid request due to validation errors", 422),

    InvalidAppVersionException(2002, "invalid App Version", 412),
    InvalidAppVersionPlatformException(2003, "Invalid App platform", 412),

    ResourceNotFoundException(3001, "resource not found", 404),

    InternalServerErrorException(4001, "Internal server error", 500),

    EmailAlreadyExistsException(5001, "Email already registered", 400),
    EmailMatchNewEmailException(5011, "Email match new Email", 400),
    UserNameAlreadyExistsException(5002, "Username already exists", 400),

    AccountNotFoundException(5003, "Account not found", 400),

    InvalidPasswordException(5004, "Invalid password", 400),
    PasswordMatchNewPasswordException(5005, "Password match new Password", 400),
    PasswordMatchUserNameException(5006, "Password match userName", 400),
    StreamHasWatchersException(5007, "Stream has watchers you cant remove it", 400),

    FollowingBlockedUserException(5016, "Cannot follow users you blocked", 400),

    ForbiddenRequestException(6001, "Forbidden request", 403),
    StreamRemovedForbiddenRequestException(6002, "Stream removed forbidden request", 403),

    ContributorNumberStreamException(5018, "It has surpassed the number of contributors for this stream", 400);



    private int code;
    private int httpCode;
    private String description;

    ErrorInfo(int code, String description, int httpCode) {
        this.code = code;
        this.description = description;
        this.httpCode = httpCode;
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    public int httpCode() {
        return httpCode;
    }

    public static ErrorInfo getForHttpStatusAndCode(int httpCode, int code) {
        for (ErrorInfo e : ErrorInfo.values()) {
            if (e.httpCode == httpCode && e.code == code) {
                return e;
            }
        }
        return null;
    }

    @Override public String toString() {
        return "ErrorInfo{" +
          "code=" + code +
          ", httpCode=" + httpCode +
          ", description='" + description + '\'' +
          '}';
    }
}
