package org.checkerframework.framework.util.defaults;

import static java.util.Comparator.comparing;

import java.util.Comparator;
import java.util.Objects;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.TypeUseLocation;
import org.checkerframework.framework.util.defaults.QualifierDefaults.AdditionalTypeUseLocation;
import org.checkerframework.javacutil.AnnotationUtils;

/**
 * Represents a mapping from an Annotation to a TypeUseLocation it should be applied to during
 * defaulting. The Comparable ordering of this class first tests location then tests annotation
 * ordering (via {@link org.checkerframework.javacutil.AnnotationUtils}).
 *
 * <p>It also has a handy toString method that is useful for debugging.
 */
public class Default implements Comparable<Default> {
  // please remember to add any fields to the hashcode calculation
  public final AnnotationMirror anno;
  public final Enum<?> location;

  public Default(final AnnotationMirror anno, final Enum<?> location) {
    if (!(location instanceof TypeUseLocation)
        && !(location instanceof AdditionalTypeUseLocation)) {
      throw new IllegalArgumentException(
          "location argument "
              + location
              + " has unrecognized class "
              + location.getDeclaringClass().getName());
    }
    this.anno = anno;
    this.location = location;
  }

  @Override
  public int compareTo(Default other) {
    int locationOrder = TYPE_USE_LOCATION_COMPARATOR.compare(location, other.location);
    if (locationOrder == 0) {
      return AnnotationUtils.compareAnnotationMirrors(anno, other.anno);
    } else {
      return locationOrder;
    }
  }

  @Override
  public boolean equals(@Nullable Object thatObj) {
    if (thatObj == this) {
      return true;
    }

    if (thatObj == null || thatObj.getClass() != Default.class) {
      return false;
    }

    return compareTo((Default) thatObj) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(anno, location);
  }

  @Override
  public String toString() {
    return "( " + location.name() + " => " + anno + " )";
  }

  /**
   * Compares TypeUseLocation and AdditionalTypeUseLocation instances. Puts
   * AdditionalTypeUseLocation first (so that it doesn't lose out to TypeUseLocation.OTHERWISE/ALL).
   * Then compares within a single enum type using its usual order.
   */
  @SuppressWarnings("unchecked")
  private static final Comparator<Enum<?>> TYPE_USE_LOCATION_COMPARATOR =
      comparing((Enum<?> e) -> e instanceof TypeUseLocation).thenComparing(e -> (Enum) e);
}
