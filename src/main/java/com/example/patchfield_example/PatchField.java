package com.example.patchfield_example;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A container object which indicates whether a value is provided or not.
 * If a value is provided (null or non-null values), {@link #isProvided()} returns {@code true}.
 * If no value is provided, {@link #isProvided()} returns {@code false}.
 * <p></p>
 * This class is intended to be used to wrap field types in a PATCH request,
 * where you need to identify whether a field is provided in the request body or not.
 *
 * <p>Usage example:
 * <pre>
 * public class ExampleRequest {
 *     private PatchField<String> name = PatchField.notProvided();
 *
 *     @JsonSetter(nulls = Nulls.SET)
 *     public void setName(String name) {
 *         this.name = PatchField.of(name);
 *     }
 *
 *     public PatchField<String> getName() {
 *         return name;
 *     }
 * }
 * </pre>
 *
 * @param <T> the type of the value
 */
public final class PatchField<T> {
    /**
     * Singleton instance for {@link #notProvided()}.
     */
    private static final PatchField<?> NOT_PROVIDED = new PatchField<>(null, false);

    /**
     * The value, if provided.
     */
    private final T value;

    /**
     * Indicates whether this {@code PatchField} has a provided value.
     */
    private final boolean provided;

    private PatchField(T value, boolean provided) {
        this.value = value;
        this.provided = provided;
    }

    /**
     * Returns a {@code PatchField} instance describing the given value.
     *
     * @param value the value
     * @param <T> The type of the value
     * @return a {@code PatchField} with the provided value
     */
    public static <T> PatchField<T> of(T value) {
        return new PatchField<>(value, true);
    }

    /**
     * Returns a {@code PatchField} instance representing a not provided value.
     *
     * @param <T> The type of the non-provided value
     * @return a {@code PatchField} representing a not provided value
     */
    @SuppressWarnings("unchecked")
    public static <T> PatchField<T> notProvided() {
        return (PatchField<T>) NOT_PROVIDED;
    }

    /**
     * Checks if a value is provided.
     *
     * @return {@code true} if a value is provided, otherwise {@code false}
     */
    public boolean isProvided() {
        return provided;
    }

    /**
     * Retrieves the value if provided; otherwise, throws {@code NoSuchElementException}.
     *
     * @return the non-null value
     * @throws NoSuchElementException if no value is provided
     */
    public T get() {
        if (!provided) {
            throw new NoSuchElementException("No value provided");
        }
        return value;
    }

    /**
     * If provided, performs the given action with the value; otherwise, does nothing.
     *
     * @param action the action to be performed if a value is provided
     */
    public void ifProvided(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        if (provided) {
            action.accept(value);
        }
    }

    /**
     * If provided, applies the given mapping function to the value and returns a new {@code PatchField}
     * describing the result; otherwise, returns a {@code PatchField} representing a not provided value.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U> The type of the result
     * @return a {@code PatchField} instance describing the result of applying the mapping function,
     * or a {@code PatchField} instance representing a not provided value.
     */
    public <U> PatchField<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return provided ? PatchField.of(mapper.apply(value)) : notProvided();
    }

    /**
     * If provided, validates the value with the given predicate; if the validation fails,
     * the specified exception is thrown.
     *
     * @param predicate the predicate to apply to the value if provided
     * @param exception the exception to be thrown if validation fails
     * @param <X> the type of the exception to be thrown if validation fails
     * @return this {@code PatchField} instance
     * @throws X if validation fails
     */
    public <X extends Throwable> PatchField<T> ifProvidedValidate(Predicate<? super T> predicate, X exception) throws X {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(exception);
        if (provided && !predicate.test(value)) {
            throw exception;
        }
        return this;
    }

    @Override
    public String toString() {
        return provided ? "PatchField[" + value + "]" : "PatchField.notProvided";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatchField<?> that)) return false;
        return provided == that.provided && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, provided);
    }
}
