package com.backend.find_crime.config.security.OAuth2;

import lombok.Getter;

public class KakaoDTO {

    @Getter
    public static class OAuthToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
        private int refresh_token_expires_in;
    }

    @Getter
    public static class KakaoProfile {
        private Long id;
        private String connected_at;
        private Account kakao_account;

        @Getter
        public static class Account {
            private String email;
            private Profile profile;

            @Getter
            public static class Profile {
                private String nickname;
                private String profile_image_url;
            }
        }
    }
}