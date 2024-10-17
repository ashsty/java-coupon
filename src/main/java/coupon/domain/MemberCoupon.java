package coupon.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
public class MemberCoupon {

    private static final Logger log = LoggerFactory.getLogger(MemberCoupon.class);

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private final Long id;
    private final Long couponId;
    private final Long memberId;
    private final LocalDateTime grantDateTime;
    private final LocalDateTime expireDateTime;
    private boolean isUsed;

    public MemberCoupon(Long id, Long couponId, Long memberId, boolean isUsed, LocalDateTime grantDateTime) {
        this.id = id;
        this.couponId = couponId;
        this.memberId = memberId;
        this.isUsed = isUsed;
        this.grantDateTime = grantDateTime;
        this.expireDateTime = calculateExpireDateTime();
    }

    public MemberCoupon(Long couponId, Long memberId, boolean isUsed, LocalDateTime grantDateTime) {
        this(null, couponId, memberId, isUsed, grantDateTime);
    }

    public boolean isUsable(LocalDateTime requestTime) {
        return !requestTime.isAfter(expireDateTime) && isNotUsed();
    }

    private boolean isNotUsed() {
        return !isUsed;
    }

    public void use(Long userId) {
        if (!userId.equals(memberId)) {
            log.warn("쿠폰 소유자가 아닌 사람이 쿠폰 사용을 시도했습니다.");
            throw new IllegalArgumentException("쿠폰 주인이 아닙니다.");
        }
        isUsed = true;
        log.info("쿠폰이 사용되었습니다: " + id + "(사용자: " + memberId + ")");
    }

    private LocalDateTime calculateExpireDateTime() {
        LocalTime expireTime = LocalTime.of(23, 59, 59, 999999000);
        return grantDateTime.plusDays(6).with(expireTime);
    }
}