package knbit.events.bc.common;

/**
 * Created by novy on 05.05.15.
 */
public interface IdentifiedObject<ID extends DomainIdentifier> {

    ID id();

}
